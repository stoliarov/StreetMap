package ru.nsu.stoliarov.streetmap.persistence.jpa.repository.way;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way.WayNodeEntity;

import java.util.List;

@Repository
public interface WayNodeRepository extends JpaRepository<WayNodeEntity, Long> {

    @Query(
            "select node.nodeId from WayNodeEntity node " +
                    "where node.wayId = ?1 " +
                    "order by node.order asc"
    )
    List<Long> findNodesOfWay(Long wayId);

    void deleteAllByWayId(Long id);
}

