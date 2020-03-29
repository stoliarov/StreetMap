package ru.nsu.stoliarov.streetmap.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.stoliarov.streetmap.model.ICountHolder;

@Data
@NoArgsConstructor
public class EntriesCountByUserResponse implements ICountHolder {
	
	private String userName;
	
	private Long entriesCount;
	
	@Override
	public String getKey() {
		return this.userName;
	}
	
	@Override
	public void setKey(String key) {
		this.userName = key;
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
