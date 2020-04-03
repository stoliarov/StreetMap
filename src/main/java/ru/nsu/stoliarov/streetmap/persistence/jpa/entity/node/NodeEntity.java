package ru.nsu.stoliarov.streetmap.persistence.jpa.entity.node;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(schema = "osm", name = "node")
public class NodeEntity {

    @Id
    private Long id;

    private String version;

    private ZonedDateTime dateTime;

    private Long uid;

    private String userName;

    private Long changeSet;

    private Double lat;

    private Double lon;
}
