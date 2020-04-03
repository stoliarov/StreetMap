package ru.nsu.stoliarov.streetmap.persistence.jpa.converter;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.Relation;
import ru.nsu.stoliarov.streetmap.model.RelationMember;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation.RelationEntity;

import java.util.List;

@Service
@NoArgsConstructor
public class RelationEntityConverter {

    public Relation convert(RelationEntity relation,
                            List<RelationMember> members,
                            List<Pair<String, String>> tags) {

        if (relation == null) {
            return null;
        }

        Relation result = new Relation();

        result.setId(relation.getId());
        result.setVersion(relation.getVersion());
        result.setDateTime(relation.getDateTime());
        result.setUid(relation.getUid());
        result.setUserName(relation.getUserName());
        result.setChangeSet(relation.getChangeSet());

        result.setMember(members);
        result.setTags(tags);

        return result;
    }

    public RelationEntity convert(Relation relation) {

        if (relation == null) {
            return null;
        }

        RelationEntity result = new RelationEntity();

        result.setId(relation.getId());
        result.setVersion(relation.getVersion());
        result.setDateTime(relation.getDateTime());
        result.setUid(relation.getUid());
        result.setUserName(relation.getUserName());
        result.setChangeSet(relation.getChangeSet());

        return result;
    }
}
