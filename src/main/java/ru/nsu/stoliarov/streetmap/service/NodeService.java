package ru.nsu.stoliarov.streetmap.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.api.model.NodeUpdateResponse;
import ru.nsu.stoliarov.streetmap.model.Node;
import ru.nsu.stoliarov.streetmap.api.model.StatusHolder;
import ru.nsu.stoliarov.streetmap.persistence.jpa.converter.NodeEntityConverter;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.node.NodeEntity;
import ru.nsu.stoliarov.streetmap.persistence.jpa.repository.node.NodeRepository;

@Service
@AllArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    private final NodeEntityConverter nodeConverter;

    public Node getNode(Long id) {

        if (id == null) {
            return null;
        }

        return nodeRepository.findById(id)
                .map(nodeConverter::convert)
                .orElse(null);
    }

    public StatusHolder saveNode(Node node) {

        NodeEntity nodeEntity = nodeConverter.convert(node);

        if (nodeEntity == null) {
            return StatusHolder.buildFaultStatus("Failed to save node: null");
        }

        nodeRepository.save(nodeEntity);

        return StatusHolder.buildSuccessStatus();
    }

    public StatusHolder deleteNode(Long id) {

        if (id == null) {
            return StatusHolder.buildFaultStatus("Failed to delete node with id: null");
        }

        if (nodeRepository.existsById(id)) {
            nodeRepository.deleteById(id);
        }

        return StatusHolder.buildSuccessStatus();
    }

    public NodeUpdateResponse updateNode(Node node) {

        NodeEntity nodeEntity = nodeConverter.convert(node);

        if (nodeEntity == null) {
            return new NodeUpdateResponse(StatusHolder.buildFaultStatus("Failed to update node: null"));
        }

        NodeEntity saved = nodeRepository.save(nodeEntity);

        return new NodeUpdateResponse(nodeConverter.convert(saved));
    }
}
