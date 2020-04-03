package ru.nsu.stoliarov.streetmap.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.api.model.RelationUpdateResponse;
import ru.nsu.stoliarov.streetmap.api.model.StatusHolder;
import ru.nsu.stoliarov.streetmap.model.Relation;
import ru.nsu.stoliarov.streetmap.service.RelationService;

@RestController
@AllArgsConstructor
@RequestMapping("/db/relation")
public class RelationController {

    private final RelationService relationService;

    @GetMapping("/{id}")
    public Relation getRelation(@PathVariable Long id) {
        return relationService.getRelation(id);
    }

    @PostMapping("/add")
    public StatusHolder addRelation(@RequestBody Relation relation) {
        return relationService.saveRelation(relation);
    }

    @DeleteMapping("/{id}")
    public StatusHolder deleteRelation(@PathVariable Long id) {
        return relationService.deleteRelation(id);
    }

    @PostMapping("/update")
    public RelationUpdateResponse updateRelation(@RequestBody Relation relation) {
        return relationService.updateRelation(relation);
    }
}
