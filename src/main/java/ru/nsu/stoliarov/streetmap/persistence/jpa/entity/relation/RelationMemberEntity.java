package ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(schema = "osm", name = "relation_member")
public class RelationMemberEntity {

    @Id
    private Long id;

    private String type;

    private String role;

    @Column(name = "relation_id")
    private Long relationId;

    private Long ref;
}
