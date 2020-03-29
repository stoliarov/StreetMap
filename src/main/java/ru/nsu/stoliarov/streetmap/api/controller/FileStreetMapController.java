package ru.nsu.stoliarov.streetmap.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.model.ICountHolder;
import ru.nsu.stoliarov.streetmap.service.osm.StreetMapElementService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileStreetMapController {
	
	private StreetMapElementService streetMapElementService;
	
	@GetMapping
	@RequestMapping("/node-count-by-user")
	public List<? extends ICountHolder> getEntriesCountByUser() {
		return streetMapElementService.getEntriesCountByUser();
	}
	
	@GetMapping
	@RequestMapping("/node-count-by-uid")
	public List<? extends ICountHolder> getEntriesCountByUid() {
		return streetMapElementService.getEntriesCountByUid();
	}
}
