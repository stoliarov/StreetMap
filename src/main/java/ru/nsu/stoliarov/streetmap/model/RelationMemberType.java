package ru.nsu.stoliarov.streetmap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RelationMemberType {
	
	WAY("way"),
	
	NODE("node");
	
	public static RelationMemberType findByName(String name) {
		
		for(RelationMemberType value : RelationMemberType.values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		
		return null;
	}
	
	private String name;
}
