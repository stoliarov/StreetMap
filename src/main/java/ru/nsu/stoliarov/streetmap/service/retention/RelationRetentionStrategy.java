package ru.nsu.stoliarov.streetmap.service.retention;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.generated.Osm;
import ru.nsu.stoliarov.streetmap.model.Relation;
import ru.nsu.stoliarov.streetmap.model.RetentionOptions;
import ru.nsu.stoliarov.streetmap.service.converter.RelationConverter;
import ru.nsu.stoliarov.streetmap.service.persistence.dao.RelationDao;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

@Slf4j
@Service
public class RelationRetentionStrategy implements IRetentionStrategy {
	
	private final Unmarshaller unmarshaller;
	
	private final RelationConverter relationConverter;
	
	private final RelationDao relationDao;
	
	public RelationRetentionStrategy(RelationConverter relationConverter,
	                                 RelationDao relationDao) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Osm.Relation.class);
		this.unmarshaller = jaxbContext.createUnmarshaller();
		this.relationConverter = relationConverter;
		this.relationDao = relationDao;
	}
	
	@Override
	public void saveStreetMapElement(XMLStreamReader reader, RetentionOptions retentionOptions) {
		
		try {
			JAXBElement<Osm.Relation> osmRelation = unmarshaller.unmarshal(reader, Osm.Relation.class);
			
			Relation relation = relationConverter.convert(osmRelation.getValue());
			
			if (relation == null) {
				log.warn("Failed to convert relation: {}", osmRelation);
				return;
			}
			
			relationDao.save(relation);
			
		} catch (JAXBException e) {
			log.warn("Failed to save street map element: {}", reader.getName());
			e.printStackTrace();
		}
	}

	@Override
	public void flush() {}
}
