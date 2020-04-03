package ru.nsu.stoliarov.streetmap.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.api.model.EntriesCountByUidResponse;
import ru.nsu.stoliarov.streetmap.api.model.EntriesCountByUserResponse;
import ru.nsu.stoliarov.streetmap.api.model.SpeedTestRequest;
import ru.nsu.stoliarov.streetmap.api.model.SpeedTestResponse;
import ru.nsu.stoliarov.streetmap.api.model.ICountHolder;
import ru.nsu.stoliarov.streetmap.retention.model.RetentionOptions;
import ru.nsu.stoliarov.streetmap.api.model.StatusHolder;
import ru.nsu.stoliarov.streetmap.model.StreetMapElement;
import ru.nsu.stoliarov.streetmap.file.bzip.util.BZipFileUncompressor;
import ru.nsu.stoliarov.streetmap.persistence.dao.NodeDao;
import ru.nsu.stoliarov.streetmap.persistence.dao.RelationDao;
import ru.nsu.stoliarov.streetmap.persistence.dao.SaveMode;
import ru.nsu.stoliarov.streetmap.persistence.dao.WayDao;
import ru.nsu.stoliarov.streetmap.retention.service.IRetentionStrategy;
import ru.nsu.stoliarov.streetmap.retention.service.RetentionStrategiesHolder;
import ru.nsu.stoliarov.streetmap.speedtest.SpeedTestDataHolder;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StreetMapElementService {

    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private static final String HUGE_BZIP_FILE_NAME = "RU-NVS.osm.bz2";

    private static final String SHORT_XML_FILE_NAME = "RU-NVS-short.osm";

    private static final String XML_SPEED_TEST_FILE_NAME = "RU-NVS-speed-test.osm";

    private static final String USER_ATTRIBUTE = "user";

    private static final String UID_ATTRIBUTE = "uid";

    private final RetentionStrategiesHolder retentionStrategiesHolder;

    private final SpeedTestDataHolder speedTestDataHolder;

    private final NodeDao nodeDao;

    private final WayDao wayDao;

    private final RelationDao relationDao;

    public SpeedTestResponse testSpeed(SpeedTestRequest request) {

        int batchSize = Optional.ofNullable(request)
                .map(SpeedTestRequest::getBatchSize)
                .orElse(100);

        SpeedTestResponse result = new SpeedTestResponse();
        result.setResult(new ArrayList<>());
        result.setBatchSize(batchSize);

        clearDb();

        result.getResult().add(testSpeed(SaveMode.STATEMENT, batchSize));
        result.getResult().add(testSpeed(SaveMode.PREPARED_STATEMENT, batchSize));
        result.getResult().add(testSpeed(SaveMode.BATCH, batchSize));

        return result;
    }

    public StatusHolder persistToDbShortFile(RetentionOptions retentionOptions) {
        return persistToDb(retentionOptions, SHORT_XML_FILE_NAME, false);
    }

    public StatusHolder persistToDbHugeFile(RetentionOptions retentionOptions) {
        return persistToDb(retentionOptions, HUGE_BZIP_FILE_NAME, true);
    }

    public StatusHolder clearDb() {

        nodeDao.deleteAll();
        wayDao.deleteAll();
        relationDao.deleteAll();

        return StatusHolder.buildSuccessStatus();
    }

    public List<? extends ICountHolder> getEntriesCountByUser() {

        return getNodesCountByAttribute(USER_ATTRIBUTE, EntriesCountByUserResponse::new);
    }

    public List<? extends ICountHolder> getEntriesCountByUid() {

        return getNodesCountByAttribute(UID_ATTRIBUTE, EntriesCountByUidResponse::new);
    }

    private StatusHolder persistToDb(RetentionOptions retentionOptions, String fileName, boolean isBzip) {

        try {

            XMLStreamReader reader = isBzip ?
                    createXmlStreamReaderFromBzip(fileName) : createXmlStreamReaderFromXml(fileName);

            if (reader == null) {
                return StatusHolder.buildFaultStatus("Failed to create read file: " + fileName);
            }

            while (reader.hasNext()) {

                List<StreetMapElement> allowedTagNames = Arrays.asList(
                        StreetMapElement.NODE,
                        StreetMapElement.WAY,
                        StreetMapElement.RELATION
                );

                if (!findElement(reader, allowedTagNames)) {
                    continue;
                }

                StreetMapElement streetMapElement = getStreetMapElement(reader);
                saveStreetMapElement(streetMapElement, retentionOptions, reader);
            }

        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        retentionStrategiesHolder.getStrategies().forEach((k, v) -> v.flush());

        return StatusHolder.buildSuccessStatus();
    }

    private void saveStreetMapElement(StreetMapElement streetMapElement,
                                      RetentionOptions retentionOptions,
                                      XMLStreamReader reader) {

        if (streetMapElement == null) {
            log.warn("Failed to save street map element: {}", reader.getName());
            return;
        }

        IRetentionStrategy strategy = retentionStrategiesHolder.getStrategies().get(streetMapElement);

        if (strategy == null) {
            log.warn("Failed to save street map element: {}", reader.getName());
            return;
        }

        strategy.saveStreetMapElement(reader, retentionOptions);
    }

    private SpeedTestResponse.ResultItem testSpeed(SaveMode saveMode, int batchSize) {

        speedTestDataHolder.reset();

        RetentionOptions options = new RetentionOptions(saveMode, batchSize);
        persistToDb(options, XML_SPEED_TEST_FILE_NAME, false);
        clearDb();

        Long recordCount = speedTestDataHolder.getRecordCount();
        Long milliseconds = speedTestDataHolder.getMilliseconds();

        if (recordCount == null || milliseconds == null) {
            return new SpeedTestResponse.ResultItem();
        }

        double recordPerSecond = ((double) recordCount) / (milliseconds / 1000.0);

        return new SpeedTestResponse.ResultItem(saveMode, recordCount, recordPerSecond);
    }

    private List<? extends ICountHolder> getNodesCountByAttribute(String attributeName,
                                                                  Supplier<? extends ICountHolder> resultSupplier) {

        try {
            XMLStreamReader reader = createXmlStreamReaderFromBzip(HUGE_BZIP_FILE_NAME);

            if (reader == null) {
                return Collections.emptyList();
            }

            Map<String, Long> attributeToEntryCount = new HashMap<>();

            while (reader.hasNext()) {

                if (!findElementWithAttribute(reader, StreetMapElement.NODE, attributeName)) {
                    continue;
                }

                String userName = reader.getAttributeValue("", attributeName);

                long count = attributeToEntryCount.containsKey(userName) ? attributeToEntryCount.get(userName) + 1 : 1;
                attributeToEntryCount.put(userName, count);
            }

            return convertToCountResponse(attributeToEntryCount, resultSupplier);

        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private boolean findElement(XMLStreamReader reader,
                                List<StreetMapElement> allowedTagNames) throws XMLStreamException {

        return findElementWithAttribute(reader, allowedTagNames, null);
    }

    private boolean findElementWithAttribute(XMLStreamReader reader,
                                             StreetMapElement element,
                                             String allowedAttribute) throws XMLStreamException {

        return findElementWithAttribute(reader, Collections.singletonList(element), allowedAttribute);
    }

    private boolean findElementWithAttribute(XMLStreamReader reader,
                                             List<StreetMapElement> allowedElements,
                                             String allowedAttribute) throws XMLStreamException {

        int eventType = reader.next();

        if (eventType != XMLStreamConstants.START_ELEMENT) {
            return false;
        }

        StreetMapElement element = getStreetMapElement(reader);

        if (!allowedElements.contains(element)) {
            return false;
        }

        if (allowedAttribute == null) {
            return true;
        }

        String attributeValue = reader.getAttributeValue("", allowedAttribute);

        return !StringUtils.isBlank(attributeValue);
    }

    private StreetMapElement getStreetMapElement(XMLStreamReader reader) {

        return Optional.of(reader)
                .map(XMLStreamReader::getName)
                .map(QName::getLocalPart)
                .map(StreetMapElement::findByName)
                .orElse(null);
    }

    private List<? extends ICountHolder> convertToCountResponse(Map<String, Long> userNameToEntryCount,
                                                                Supplier<? extends ICountHolder> resultSupplier) {

        return userNameToEntryCount.entrySet().stream()
                .map(entry -> {
                    ICountHolder result = resultSupplier.get();
                    result.setKey(entry.getKey());
                    result.setCount(entry.getValue());
                    return result;
                })
                .sorted((first, second) -> first.getCount() > second.getCount() ? -1 :
                        first.getCount().equals(second.getCount()) ? 0 : 1)
                .collect(Collectors.toList());
    }

    @Nullable
    private XMLStreamReader createXmlStreamReaderFromBzip(String fileName) throws IOException, XMLStreamException {

        BZipFileUncompressor bZipFileUncompressor = new BZipFileUncompressor(fileName);

        InputStream uncompressedInputStream = bZipFileUncompressor.startUncompressing();

        if (uncompressedInputStream == null) {
            log.error("Failed to init uncompressing of file: {}", fileName);
            return null;
        }

        return FACTORY.createXMLStreamReader(uncompressedInputStream);
    }

    @Nullable
    private XMLStreamReader createXmlStreamReaderFromXml(String fileName) throws IOException, XMLStreamException {

        InputStream fileInputStream = Files.newInputStream(Paths.get(fileName));

        return FACTORY.createXMLStreamReader(fileInputStream);
    }
}
