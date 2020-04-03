package ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(schema = "osm", name = "way")
public class WayEntity {

    @Id
    private Long id;

    private String version;

    private ZonedDateTime dateTime;

    private Long uid;

    private String userName;

    private Long changeSet;
}
