package ru.nsu.stoliarov.streetmap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface ICountHolder {
	
	@JsonIgnore
	String getKey();
	
	void setKey(String key);
	
	@JsonIgnore
	Long getCount();
	
	void setCount(Long count);
}
