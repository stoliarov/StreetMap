package ru.nsu.stoliarov.streetmap.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.EntriesCountByUidResponse;
import ru.nsu.stoliarov.streetmap.model.EntriesCountByUserResponse;
import ru.nsu.stoliarov.streetmap.model.ICountHolder;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class NodeService {
	
	private static final String FILE_NAME = "RU-NVS.osm.bz2";
	
	private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
	
	private static final String NODE_TAG = "node";
	
	private static final String USER_ATTRIBUTE = "user";
	
	private static final String UID_ATTRIBUTE = "uid";
	
	public List<? extends ICountHolder> getEntriesCountByUser() {
		
		return getNodesCountByAttribute(USER_ATTRIBUTE, EntriesCountByUserResponse::new);
	}
	
	public List<? extends ICountHolder> getEntriesCountByUid() {
		
		return getNodesCountByAttribute(UID_ATTRIBUTE, EntriesCountByUidResponse::new);
	}
	
	private List<? extends ICountHolder> getNodesCountByAttribute(String attributeName,
	                                                              Supplier<? extends ICountHolder> resultSupplier) {
		
		try {
			XMLStreamReader reader = createXmlStreamReader(FILE_NAME);
			
			if (reader == null) {
				return Collections.emptyList();
			}
			
			Map<String, Long> attributeToEntryCount = new HashMap<>();
			
			int i = 0;
			
			while((reader.hasNext() && i++ < 100000)) {
				
				if (!isTagWithNameAndAttribute(reader, NODE_TAG, attributeName)) {
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
	
	private boolean isTagWithNameAndAttribute(XMLStreamReader reader,
	                                          String tag,
	                                          String attribute) throws XMLStreamException {
		
		int eventType = reader.next();
		
		if (eventType != XMLStreamConstants.START_ELEMENT) {
			return false;
		}
		
		String tagName = Optional.of(reader)
				.map(XMLStreamReader::getName)
				.map(QName::getLocalPart)
				.orElse("");
		
		
		if (!tag.equals(tagName)) {
			return false;
		}
		
		String userName = reader.getAttributeValue("", attribute);
		
		return !StringUtils.isBlank(userName);
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
	private XMLStreamReader createXmlStreamReader(String fileName) throws IOException, XMLStreamException {
		
		BZipFileUncompressor bZipFileUncompressor = new BZipFileUncompressor(fileName);
		
		InputStream uncompressedInputStream = bZipFileUncompressor.startUncompressing();
		
		if (uncompressedInputStream == null) {
			log.error("Failed to init uncompressing of file: {}", fileName);
			return null;
		}
		
		return FACTORY.createXMLStreamReader(uncompressedInputStream);
	}
}
