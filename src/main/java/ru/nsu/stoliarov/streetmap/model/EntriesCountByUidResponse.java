package ru.nsu.stoliarov.streetmap.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EntriesCountByUidResponse implements ICountHolder {
	
	private String uid;
	
	private Long entriesCount;
	
	@Override
	public String getKey() {
		return this.uid;
	}
	
	@Override
	public void setKey(String key) {
		this.uid = key;
	}
	
	@Override
	public Long getCount() {
		return this.entriesCount;
	}
	
	@Override
	public void setCount(Long count) {
		this.entriesCount = count;
	}
}
