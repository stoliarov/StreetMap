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
public class Way {
	
	protected Long id;
	
	protected String version;
	
	protected ZonedDateTime dateTime;
	
	protected Long uid;
	
	protected String userName;
	
	protected Long changeSet;
	
	protected List<Long> nodeIds;
	
	protected List<Pair<String, String>> tags;
}
