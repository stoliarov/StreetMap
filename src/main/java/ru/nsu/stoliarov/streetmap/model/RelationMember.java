package ru.nsu.stoliarov.streetmap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationMember {
	
	private RelationMemberType type;
	
	private Long ref;
	
	private RelationMemberRole role;
}
