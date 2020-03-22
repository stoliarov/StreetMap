package ru.nsu.stoliarov.streetmap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum StreetMapElement {
	
	NODE("node"),
	
	WAY("way"),
	
	RELATION("relation");
	
	public static StreetMapElement findByName(String name) {
		
		for(StreetMapElement value : StreetMapElement.values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		
		return null;
	}
	
	private String name;
}
