package ru.nsu.stoliarov.streetmap.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nsu.stoliarov.streetmap.model.Node;

@Data
@EqualsAndHashCode(callSuper = true)
public class NodeUpdateResponse extends StatusHolder {

    private Node node;

    public NodeUpdateResponse(Node node) {
        super(StatusHolder.buildSuccessStatus());
        this.node = node;
    }

    public NodeUpdateResponse(StatusHolder statusHolder) {
        super(statusHolder);
    }
}

