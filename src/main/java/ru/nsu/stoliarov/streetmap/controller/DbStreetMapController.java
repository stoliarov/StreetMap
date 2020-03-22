package ru.nsu.stoliarov.streetmap.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.service.osm.StreetMapElementService;

@RestController
@AllArgsConstructor
@RequestMapping("/db")
public class DbStreetMapController {
	
	private StreetMapElementService streetMapElementService;
	
	@PostMapping
	@RequestMapping("/persist")
	public Boolean persistToDb() {
		return streetMapElementService.persistToDb();
	}
	
}
