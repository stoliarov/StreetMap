package ru.nsu.stoliarov.streetmap.persistence.jpa.repository.way;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way.WayEntity;

@Repository
public interface WayRepository extends JpaRepository<WayEntity, Long> {

}
