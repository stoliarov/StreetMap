package ru.nsu.stoliarov.streetmap.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nsu.stoliarov.streetmap.model.Relation;

@Data
@EqualsAndHashCode(callSuper = true)
public class RelationUpdateResponse extends StatusHolder {

    private Relation relation;

    public RelationUpdateResponse(Relation relation) {
        super(StatusHolder.buildSuccessStatus());
        this.relation = relation;
    }

    public RelationUpdateResponse(StatusHolder statusHolder) {
        super(statusHolder);
    }
}
