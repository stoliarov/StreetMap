package ru.nsu.stoliarov.streetmap.service.retention;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.StreetMapElement;

import java.util.HashMap;
import java.util.Map;

@Getter
@Service
public class RetentionStrategiesHolder {
	
	private final Map<StreetMapElement, IRetentionStrategy> strategies;
	
	public RetentionStrategiesHolder(NodeRetentionStrategy nodeRetentionStrategy,
	                                 WayRetentionStrategy wayRetentionStrategy,
	                                 RelationRetentionStrategy relationRetentionStrategy) {
		
		this.strategies = new HashMap<>();
		this.strategies.put(StreetMapElement.NODE, nodeRetentionStrategy);
		this.strategies.put(StreetMapElement.WAY, wayRetentionStrategy);
		this.strategies.put(StreetMapElement.RELATION, relationRetentionStrategy);
	}
}
