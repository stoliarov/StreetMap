package ru.nsu.stoliarov.streetmap.persistence.jpa.converter;

import lombok.NoArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.Way;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way.WayEntity;

import java.util.List;

@Service
@NoArgsConstructor
public class WayEntityConverter {

    public Way convert(WayEntity wayEntity, List<Long> nodeIds, List<Pair<String, String>> tags) {

        if (wayEntity == null) {
            return null;
        }

        Way way = new Way();

        way.setId(wayEntity.getId());
        way.setVersion(wayEntity.getVersion());
        way.setDateTime(wayEntity.getDateTime());
        way.setUid(wayEntity.getUid());
        way.setUserName(wayEntity.getUserName());
        way.setChangeSet(wayEntity.getChangeSet());

        way.setNodeIds(ListUtils.emptyIfNull(nodeIds));
        way.setTags(tags);

        return way;
    }

    public WayEntity convert(Way way) {

        if (way == null) {
            return null;
        }

        WayEntity result = new WayEntity();

        result.setId(way.getId());
        result.setVersion(way.getVersion());
        result.setDateTime(way.getDateTime());
        result.setUid(way.getUid());
        result.setUserName(way.getUserName());
        result.setChangeSet(way.getChangeSet());

        return result;
    }
}
