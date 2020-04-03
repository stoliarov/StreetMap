package ru.nsu.stoliarov.streetmap.persistence.jpa.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation.RelationTagEntity;

import java.util.List;

@Repository
public interface RelationTagRepository extends JpaRepository<RelationTagEntity, Long> {

    List<RelationTagEntity> findAllByRelationId(Long id);
}
