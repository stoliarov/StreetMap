package ru.nsu.stoliarov.streetmap.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.api.model.WayUpdateResponse;
import ru.nsu.stoliarov.streetmap.api.model.StatusHolder;
import ru.nsu.stoliarov.streetmap.model.Way;
import ru.nsu.stoliarov.streetmap.service.WayService;

@RestController
@AllArgsConstructor
@RequestMapping("/db/way")
public class WayController {

    private final WayService wayService;

    @GetMapping("/{id}")
    public Way getWay(@PathVariable Long id) {
        return wayService.getWay(id);
    }

    @PostMapping("/add")
    public StatusHolder addWay(@RequestBody Way way) {
        return wayService.saveWay(way);
    }

    @DeleteMapping("/{id}")
    public StatusHolder deleteWay(@PathVariable Long id) {
        return wayService.deleteWay(id);
    }

    @PostMapping("/update")
    public WayUpdateResponse updateWay(@RequestBody Way way) {
        return wayService.updateWay(way);
    }
}
