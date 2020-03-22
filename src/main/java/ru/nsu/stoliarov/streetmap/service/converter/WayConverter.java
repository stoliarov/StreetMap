package ru.nsu.stoliarov.streetmap.service.converter;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.generated.Osm;
import ru.nsu.stoliarov.streetmap.model.Way;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WayConverter {
	
	private final DateConverter dateConverter;
	
	public Way convert(Osm.Way osmWay) {
		
		if (osmWay == null) {
			return null;
		}
		
		Way result = new Way();
		
		result.setId(osmWay.getId());
		result.setVersion(osmWay.getVersion());
		
		result.setDateTime(dateConverter.convert(osmWay.getTimestamp()));
		
		result.setUid(osmWay.getUid());
		result.setUserName(osmWay.getUser());
		result.setChangeSet(osmWay.getChangeset());
		
		result.setNodeIds(parseNodeIds(osmWay));
		result.setTags(parseTags(osmWay));
		
		return result;
	}
	
	private List<Pair<String, String>> parseTags(Osm.Way osmWay) {
		
		return osmWay.getTag().stream()
				.filter(tag -> tag.getK() != null && tag.getV() != null)
				.map(tag -> Pair.of(tag.getK(), tag.getV()))
				.collect(Collectors.toList());
	}
	
	private List<Long> parseNodeIds(Osm.Way osmWay) {
		
		return osmWay.getNd().stream()
				.map(Osm.Way.Nd::getRef)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
