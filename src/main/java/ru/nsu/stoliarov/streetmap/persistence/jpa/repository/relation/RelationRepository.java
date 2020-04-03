package ru.nsu.stoliarov.streetmap.persistence.jpa.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation.RelationEntity;

@Repository
public interface RelationRepository extends JpaRepository<RelationEntity, Long> {

}
