package ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(schema = "osm", name = "node_of_way")
public class WayNodeEntity {

    @Id
    private Long id;

    @Column(name = "node_id")
    private Long nodeId;

    @Column(name = "way_id")
    private Long wayId;

    @Column(name = "order_")
    private Long order;
}
