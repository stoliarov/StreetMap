package ru.nsu.stoliarov.streetmap.service.converter;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.generated.Osm;
import ru.nsu.stoliarov.streetmap.model.Relation;
import ru.nsu.stoliarov.streetmap.model.RelationMember;
import ru.nsu.stoliarov.streetmap.model.RelationMemberRole;
import ru.nsu.stoliarov.streetmap.model.RelationMemberType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RelationConverter {
	
	private final DateConverter dateConverter;
	
	public Relation convert(Osm.Relation osmRelation) {
		
		if (osmRelation == null) {
			return null;
		}
		
		Relation result = new Relation();
		
		result.setId(osmRelation.getId());
		result.setVersion(osmRelation.getVersion());
		
		result.setDateTime(dateConverter.convert(osmRelation.getTimestamp()));
		
		result.setUid(osmRelation.getUid());
		result.setUserName(osmRelation.getUser());
		result.setChangeSet(osmRelation.getChangeset());
		
		result.setTags(parseTags(osmRelation));
		result.setMember(parseMembers(osmRelation));
		
		return result;
	}
	
	private List<Pair<String, String>> parseTags(Osm.Relation osmRelation) {
		
		return osmRelation.getTag().stream()
				.filter(tag -> tag.getK() != null && tag.getV() != null)
				.map(tag -> Pair.of(tag.getK(), tag.getV()))
				.collect(Collectors.toList());
	}
	
	private List<RelationMember> parseMembers(Osm.Relation osmRelation) {
		
		List<RelationMember> result = new ArrayList<>();
		
		for(Osm.Relation.Member member : osmRelation.getMember()) {
			
			RelationMemberRole role = RelationMemberRole.findByName(member.getRole());
			RelationMemberType type = RelationMemberType.findByName(member.getType());
			Long refId = member.getRef();
			
			if (type == null || refId == null) {
				continue;
			}
			
			result.add(new RelationMember(type, refId, role));
		}
		
		return result;
	}
}
