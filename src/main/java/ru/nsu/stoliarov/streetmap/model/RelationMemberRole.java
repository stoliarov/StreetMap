package ru.nsu.stoliarov.streetmap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RelationMemberRole {
	
	OUTER("outer"),
	
	INNER("inner");
	
	public static RelationMemberRole findByName(String name) {
		
		for(RelationMemberRole value : RelationMemberRole.values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		
		return null;
	}
	
	private String name;
}
