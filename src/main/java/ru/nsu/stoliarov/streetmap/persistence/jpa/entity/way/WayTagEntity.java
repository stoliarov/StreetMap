package ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(schema = "osm", name = "way_tag")
public class WayTagEntity {

    @Id
    private Long id;

    @Column(name = "key_")
    private String key;

    private String value;

    @Column(name = "way_id")
    private Long wayId;
}
