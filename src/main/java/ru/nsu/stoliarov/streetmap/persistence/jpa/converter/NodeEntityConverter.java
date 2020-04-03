package ru.nsu.stoliarov.streetmap.persistence.jpa.converter;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.Node;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.node.NodeEntity;

@Service
@NoArgsConstructor
public class NodeEntityConverter {

    public Node convert(NodeEntity nodeEntity) {

        if (nodeEntity == null) {
            return null;
        }

        Node result = new Node();

        result.setId(nodeEntity.getId());
        result.setVersion(nodeEntity.getVersion());
        result.setDateTime(nodeEntity.getDateTime());
        result.setUid(nodeEntity.getUid());
        result.setUserName(nodeEntity.getUserName());
        result.setChangeSet(nodeEntity.getChangeSet());
        result.setLat(nodeEntity.getLat());
        result.setLon(nodeEntity.getLon());

        return result;
    }

    public NodeEntity convert(Node node) {

        if (node == null) {
            return null;
        }

        NodeEntity result = new NodeEntity();

        result.setId(node.getId());
        result.setVersion(node.getVersion());
        result.setDateTime(node.getDateTime());
        result.setUid(node.getUid());
        result.setUserName(node.getUserName());
        result.setChangeSet(node.getChangeSet());
        result.setLat(node.getLat());
        result.setLon(node.getLon());

        return result;
    }
}
