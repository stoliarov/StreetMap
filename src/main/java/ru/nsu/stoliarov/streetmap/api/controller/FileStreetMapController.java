package ru.nsu.stoliarov.streetmap.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.api.model.ICountHolder;
import ru.nsu.stoliarov.streetmap.service.StreetMapElementService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileStreetMapController {
	
	private StreetMapElementService streetMapElementService;
	
	@GetMapping("/node-count-by-user")
	public List<? extends ICountHolder> getEntriesCountByUser() {
		return streetMapElementService.getEntriesCountByUser();
	}
	
	@GetMapping("/node-count-by-uid")
	public List<? extends ICountHolder> getEntriesCountByUid() {
		return streetMapElementService.getEntriesCountByUid();
	}
}
