package ru.nsu.stoliarov.streetmap.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.api.model.SpeedTestRequest;
import ru.nsu.stoliarov.streetmap.model.RetentionOptions;
import ru.nsu.stoliarov.streetmap.api.model.SpeedTestResponse;
import ru.nsu.stoliarov.streetmap.service.osm.StreetMapElementService;

@RestController
@AllArgsConstructor
@RequestMapping("/db")
public class DbStreetMapController {
	
	private StreetMapElementService streetMapElementService;

	@PostMapping
	@RequestMapping("/persist-short")
	public Boolean persistShortFileToDb(@RequestBody RetentionOptions retentionOptions) {
		return streetMapElementService.persistToDbShortFile(retentionOptions);
	}

	@PostMapping
	@RequestMapping("/persist-archive")
	public Boolean persistHugeFileToDb(@RequestBody RetentionOptions retentionOptions) {
		return streetMapElementService.persistToDbHugeFile(retentionOptions);
	}

	@DeleteMapping
	@RequestMapping("/clear")
	public Boolean clearDb() {
		return streetMapElementService.clearDb();
	}

	@PostMapping
	@RequestMapping("/speed-test")
	public SpeedTestResponse testSpeed(@RequestBody SpeedTestRequest request) {
		return streetMapElementService.testSpeed(request);
	}
}
