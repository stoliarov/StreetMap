package ru.nsu.stoliarov.streetmap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node {
	
	private Long id;
	
	private String version;
	
	private ZonedDateTime dateTime;
	
	private Long uid;
	
	private String userName;
	
	private Long changeSet;
	
	private Double lat;
	
	private Double lon;
}
