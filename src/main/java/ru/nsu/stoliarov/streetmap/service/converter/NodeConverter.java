package ru.nsu.stoliarov.streetmap.service.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.generated.Osm;
import ru.nsu.stoliarov.streetmap.model.Node;

@Service
@AllArgsConstructor
public class NodeConverter {
	
	private final DateConverter dateConverter;
	
	public Node convert(Osm.Node osmNode) {
		
		if (osmNode == null) {
			return null;
		}
		
		Node result = new Node();
		
		result.setId(osmNode.getId());
		result.setVersion(osmNode.getVersion());
		
		result.setDateTime(dateConverter.convert(osmNode.getTimestamp()));
		
		result.setUid(osmNode.getUid());
		result.setUserName(osmNode.getUser());
		result.setChangeSet(osmNode.getChangeset());
		result.setLat(osmNode.getLat());
		result.setLon(osmNode.getLon());
		
		return result;
	}
}
