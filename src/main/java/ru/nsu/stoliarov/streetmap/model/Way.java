package ru.nsu.stoliarov.streetmap.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.stoliarov.streetmap.deserializer.TagDeserializer;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Way {

    protected Long id;

    protected String version;

    protected ZonedDateTime dateTime;

    protected Long uid;

    protected String userName;

    protected Long changeSet;

    protected List<Long> nodeIds;

    @JsonDeserialize(using = TagDeserializer.class)
    protected List<Pair<String, String>> tags;
}
