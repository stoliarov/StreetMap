package ru.nsu.stoliarov.streetmap.persistence.jpa.repository.way;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way.WayTagEntity;

import java.util.List;

@Repository
public interface WayTagRepository extends JpaRepository<WayTagEntity, Long> {

    List<WayTagEntity> findAllByWayId(Long wayId);
}
