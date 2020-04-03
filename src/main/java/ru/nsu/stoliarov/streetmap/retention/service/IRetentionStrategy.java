package ru.nsu.stoliarov.streetmap.retention.service;

import ru.nsu.stoliarov.streetmap.retention.model.RetentionOptions;

import javax.xml.stream.XMLStreamReader;

public interface IRetentionStrategy {
	
	void saveStreetMapElement(XMLStreamReader reader, RetentionOptions retentionOptions);

	void flush();
}
