package ru.nsu.stoliarov.streetmap.service;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.stoliarov.streetmap.api.model.WayUpdateResponse;
import ru.nsu.stoliarov.streetmap.api.model.StatusHolder;
import ru.nsu.stoliarov.streetmap.model.Way;
import ru.nsu.stoliarov.streetmap.persistence.jpa.converter.WayEntityConverter;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way.WayEntity;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way.WayNodeEntity;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.way.WayTagEntity;
import ru.nsu.stoliarov.streetmap.persistence.jpa.repository.way.WayNodeRepository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.repository.way.WayRepository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.repository.way.WayTagRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WayService {

    private final WayRepository wayRepository;

    private final WayNodeRepository wayNodeRepository;

    private final WayTagRepository wayTagRepository;

    private final WayEntityConverter wayConverter;

    public Way getWay(Long id) {

        if (id == null) {
            return null;
        }

        WayEntity wayEntity = wayRepository.findById(id).orElse(null);

        if (wayEntity == null) {
            return null;
        }

        List<Long> nodesOfWay = wayNodeRepository.findNodesOfWay(id);
        List<WayTagEntity> tagEntities = wayTagRepository.findAllByWayId(id);

        return wayConverter.convert(wayEntity, nodesOfWay, convertTags(tagEntities));
    }

    @Transactional
    public StatusHolder saveWay(Way way) {

        WayEntity wayEntity = wayConverter.convert(way);

        if (wayEntity == null) {
            return StatusHolder.buildFaultStatus("Failed to save way: null");
        }

        wayRepository.save(wayEntity);
        saveNodes(way);
        saveTags(way);

        return StatusHolder.buildSuccessStatus();
    }

    @Transactional
    public StatusHolder deleteWay(Long id) {

        if (id == null) {
            return StatusHolder.buildFaultStatus("Failed to delete way with id: null");
        }

        if (wayRepository.existsById(id)) {
            wayRepository.deleteById(id);
        }

        return StatusHolder.buildSuccessStatus();
    }

    @Transactional
    public WayUpdateResponse updateWay(Way way) {

        WayEntity wayEntity = wayConverter.convert(way);

        if (wayEntity == null) {
            return new WayUpdateResponse(StatusHolder.buildFaultStatus("Failed to update way: null"));
        }

        wayRepository.save(wayEntity);

        updateNodes(way);
        updateTags(way);

        Way updatedWay = getWay(way.getId());

        return new WayUpdateResponse(updatedWay);
    }

    private void updateNodes(Way way) {

        Long wayId = way.getId();

        List<Long> actualNodes = wayNodeRepository.findNodesOfWay(wayId);
        List<Long> newNodes = ListUtils.emptyIfNull(way.getNodeIds());

        if (!actualNodes.equals(newNodes)) {
            wayNodeRepository.deleteAllByWayId(wayId);
            saveNodes(newNodes, wayId);
        }
    }

    private void updateTags(Way way) {

        Long wayId = way.getId();

        List<WayTagEntity> actualTagEntities = wayTagRepository.findAllByWayId(wayId);
        List<Pair<String, String>> actualTags = convertTags(actualTagEntities);
        List<Pair<String, String>> newTags = way.getTags();

        newTags.stream()
                .filter(newTag -> !actualTags.contains(newTag))
                .forEach(newTag -> saveTag(newTag, wayId));

        actualTagEntities.stream()
                .filter(actualTag -> !newTags.contains(convertTag(actualTag)))
                .filter(actualTag -> wayTagRepository.existsById(actualTag.getId()))
                .forEach(actualTag -> wayTagRepository.deleteById(actualTag.getId()));
    }

    private void saveTags(Way way) {
        saveTags(way.getTags(), way.getId());
    }

    private void saveNodes(Way way) {
        saveNodes(way.getNodeIds(), way.getId());
    }

    private void saveTags(List<Pair<String, String>> tags, Long wayId) {

        ListUtils.emptyIfNull(tags).forEach(tag -> saveTag(tag, wayId));
    }

    private void saveTag(Pair<String, String> tag, Long wayId) {

        WayTagEntity wayTag = new WayTagEntity();

        wayTag.setId(RandomUtils.nextLong());
        wayTag.setKey(tag.getKey());
        wayTag.setValue(tag.getValue());
        wayTag.setWayId(wayId);

        wayTagRepository.save(wayTag);
    }

    private void saveNodes(List<Long> nodesIds, Long wayId) {

        for (int i = 0; i < ListUtils.emptyIfNull(nodesIds).size(); i++) {

            Long nodeId = nodesIds.get(i);

            WayNodeEntity wayNode = new WayNodeEntity();

            wayNode.setId(RandomUtils.nextLong());
            wayNode.setNodeId(nodeId);
            wayNode.setWayId(wayId);
            wayNode.setOrder((long) i);

            wayNodeRepository.save(wayNode);
        }
    }

    private List<Pair<String, String>> convertTags(List<WayTagEntity> tags) {

        return CollectionUtils.emptyIfNull(tags).stream()
                .filter(Objects::nonNull)
                .map(this::convertTag)
                .collect(Collectors.toList());
    }

    private Pair<String, String> convertTag(WayTagEntity tag) {
        return Pair.of(tag.getKey(), tag.getValue());
    }
}
