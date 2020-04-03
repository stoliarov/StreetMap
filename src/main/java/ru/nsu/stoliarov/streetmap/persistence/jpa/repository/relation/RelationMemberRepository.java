package ru.nsu.stoliarov.streetmap.persistence.jpa.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation.RelationMemberEntity;

import java.util.List;

@Repository
public interface RelationMemberRepository extends JpaRepository<RelationMemberEntity, Long> {

    List<RelationMemberEntity> findAllByRelationId(Long id);

    void deleteAllByRelationId(Long id);
}
