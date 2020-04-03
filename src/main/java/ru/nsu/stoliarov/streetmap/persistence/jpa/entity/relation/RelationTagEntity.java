package ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(schema = "osm", name = "relation_tag")
public class RelationTagEntity {

    @Id
    private Long id;

    @Column(name = "key_")
    private String key;

    private String value;

    @Column(name = "relation_id")
    private Long relationId;
}
