package ru.nsu.stoliarov.streetmap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relation {
	
	private Long id;
	
	private String version;
	
	private ZonedDateTime dateTime;
	
	private Long uid;
	
	private String userName;
	
	private Long changeSet;
	
	protected List<RelationMember> member;
	
	protected List<Pair<String, String>> tags;
}
