package ru.nsu.stoliarov.streetmap.retention.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.generated.Osm;
import ru.nsu.stoliarov.streetmap.retention.model.RetentionOptions;
import ru.nsu.stoliarov.streetmap.model.Way;
import ru.nsu.stoliarov.streetmap.file.xml.converter.WayConverter;
import ru.nsu.stoliarov.streetmap.persistence.dao.WayDao;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

@Slf4j
@Service
public class WayRetentionStrategy implements IRetentionStrategy {
	
	private final Unmarshaller unmarshaller;
	
	private final WayConverter wayConverter;
	
	private final WayDao wayDao;
	
	public WayRetentionStrategy(WayConverter wayConverter,
	                            WayDao wayDao) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Osm.Way.class);
		this.unmarshaller = jaxbContext.createUnmarshaller();
		this.wayConverter = wayConverter;
		this.wayDao = wayDao;
	}
	
	@Override
	public void saveStreetMapElement(XMLStreamReader reader, RetentionOptions retentionOptions) {
		
		try {
			JAXBElement<Osm.Way> osmWay = unmarshaller.unmarshal(reader, Osm.Way.class);
			
			Way way = wayConverter.convert(osmWay.getValue());
			
			if (way == null) {
				log.warn("Failed to convert way: {}", osmWay);
				return;
			}
			
			wayDao.save(way);
			
		} catch (JAXBException e) {
			log.warn("Failed to save street map element: {}", reader.getName());
			e.printStackTrace();
		}
	}

	@Override
	public void flush() {}
}
