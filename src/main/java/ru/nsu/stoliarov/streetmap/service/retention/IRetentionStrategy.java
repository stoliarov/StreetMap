package ru.nsu.stoliarov.streetmap.service.retention;

import javax.xml.stream.XMLStreamReader;

public interface IRetentionStrategy {
	
	void saveStreetMapElement(XMLStreamReader reader);
}
