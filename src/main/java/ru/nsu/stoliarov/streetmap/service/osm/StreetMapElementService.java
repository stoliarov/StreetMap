package ru.nsu.stoliarov.streetmap.service.osm;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.EntriesCountByUidResponse;
import ru.nsu.stoliarov.streetmap.model.EntriesCountByUserResponse;
import ru.nsu.stoliarov.streetmap.model.ICountHolder;
import ru.nsu.stoliarov.streetmap.model.StreetMapElement;
import ru.nsu.stoliarov.streetmap.service.file.BZipFileUncompressor;
import ru.nsu.stoliarov.streetmap.service.retention.RetentionStrategiesHolder;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StreetMapElementService {
	
	private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
	
	private static final String BZIP_FILE_NAME = "RU-NVS.osm.bz2";
	
	private static final String XML_FILE_NAME = "RU-NVS-short.osm";
	
	private static final String USER_ATTRIBUTE = "user";
	
	private static final String UID_ATTRIBUTE = "uid";
	
	private final RetentionStrategiesHolder retentionStrategiesHolder;
	
	public Boolean persistToDb() {
		
		try {
			XMLStreamReader reader = createXmlStreamReaderFromXml(XML_FILE_NAME);
			
			if (reader == null) {
				return false;
			}
			
			int i = 0;
			
			while((reader.hasNext() && i++ < 100000)) {
				
				List<StreetMapElement> allowedTagNames = Arrays.asList(
						StreetMapElement.NODE,
						StreetMapElement.WAY,
						StreetMapElement.RELATION
				);
				
				if (!findElement(reader, allowedTagNames)) {
					continue;
				}
				
				StreetMapElement streetMapElement = getStreetMapElement(reader);
				
				if (streetMapElement == null) {
					log.warn("Failed to save street map element: {}", reader.getName());
					continue;
				}
				
				retentionStrategiesHolder.getStrategies().getOrDefault(
						streetMapElement,
						it -> log.warn("Failed to save street map element: {}", reader.getName())
				).saveStreetMapElement(reader);
			}
			
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public List<? extends ICountHolder> getEntriesCountByUser() {
		
		return getNodesCountByAttribute(USER_ATTRIBUTE, EntriesCountByUserResponse::new);
	}
	
	public List<? extends ICountHolder> getEntriesCountByUid() {
		
		return getNodesCountByAttribute(UID_ATTRIBUTE, EntriesCountByUidResponse::new);
	}
	
	private List<? extends ICountHolder> getNodesCountByAttribute(String attributeName,
	                                                              Supplier<? extends ICountHolder> resultSupplier) {
		
		try {
			XMLStreamReader reader = createXmlStreamReaderFromBzip(BZIP_FILE_NAME);
			
			if (reader == null) {
				return Collections.emptyList();
			}
			
			Map<String, Long> attributeToEntryCount = new HashMap<>();
			
			int i = 0;
			
			while((reader.hasNext() && i++ < 100000)) {
				
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
