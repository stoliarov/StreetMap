package ru.nsu.stoliarov.streetmap.persistence.jpa.repository.node;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.node.NodeEntity;

@Repository
public interface NodeRepository extends JpaRepository<NodeEntity, Long> {

}
