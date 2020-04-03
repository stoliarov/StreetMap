package ru.nsu.stoliarov.streetmap.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.api.model.NodeUpdateResponse;
import ru.nsu.stoliarov.streetmap.model.Node;
import ru.nsu.stoliarov.streetmap.api.model.StatusHolder;
import ru.nsu.stoliarov.streetmap.service.NodeService;

@RestController
@AllArgsConstructor
@RequestMapping("/db/node")
public class NodeController {

    private final NodeService nodeService;

    @GetMapping("/{id}")
    public Node getNode(@PathVariable Long id) {
        return nodeService.getNode(id);
    }

    @PostMapping("/add")
    public StatusHolder addNode(@RequestBody Node node) {
        return nodeService.saveNode(node);
    }

    @DeleteMapping("/{id}")
    public StatusHolder deleteNode(@PathVariable Long id) {
        return nodeService.deleteNode(id);
    }

    @PostMapping("/update")
    public NodeUpdateResponse updateNode(@RequestBody Node node) {
        return nodeService.updateNode(node);
    }
}
