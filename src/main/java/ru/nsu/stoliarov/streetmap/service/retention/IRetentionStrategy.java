package ru.nsu.stoliarov.streetmap.service.retention;

import ru.nsu.stoliarov.streetmap.model.RetentionOptions;

import javax.xml.stream.XMLStreamReader;

public interface IRetentionStrategy {
	
	void saveStreetMapElement(XMLStreamReader reader, RetentionOptions retentionOptions);

	void flush();
}
