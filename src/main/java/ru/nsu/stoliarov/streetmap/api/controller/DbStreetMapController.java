package ru.nsu.stoliarov.streetmap.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.api.model.SpeedTestRequest;
import ru.nsu.stoliarov.streetmap.api.model.SpeedTestResponse;
import ru.nsu.stoliarov.streetmap.retention.model.RetentionOptions;
import ru.nsu.stoliarov.streetmap.api.model.StatusHolder;
import ru.nsu.stoliarov.streetmap.service.StreetMapElementService;

@RestController
@AllArgsConstructor
@RequestMapping("/db")
public class DbStreetMapController {

    private StreetMapElementService streetMapElementService;

    @PostMapping("/persist-short")
    public StatusHolder persistShortFileToDb(@RequestBody RetentionOptions retentionOptions) {
        return streetMapElementService.persistToDbShortFile(retentionOptions);
    }

    @PostMapping("/persist-archive")
    public StatusHolder persistHugeFileToDb(@RequestBody RetentionOptions retentionOptions) {
        return streetMapElementService.persistToDbHugeFile(retentionOptions);
    }

    @DeleteMapping("/clear")
    public StatusHolder clearDb() {
        return streetMapElementService.clearDb();
    }

    @PostMapping("/speed-test")
    public SpeedTestResponse testSpeed(@RequestBody SpeedTestRequest request) {
        return streetMapElementService.testSpeed(request);
    }
}
