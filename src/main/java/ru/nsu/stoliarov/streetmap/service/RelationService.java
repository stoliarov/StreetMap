package ru.nsu.stoliarov.streetmap.service;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.stoliarov.streetmap.api.model.RelationUpdateResponse;
import ru.nsu.stoliarov.streetmap.api.model.StatusHolder;
import ru.nsu.stoliarov.streetmap.model.Relation;
import ru.nsu.stoliarov.streetmap.model.RelationMember;
import ru.nsu.stoliarov.streetmap.model.RelationMemberRole;
import ru.nsu.stoliarov.streetmap.model.RelationMemberType;
import ru.nsu.stoliarov.streetmap.persistence.jpa.converter.RelationEntityConverter;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation.RelationEntity;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation.RelationMemberEntity;
import ru.nsu.stoliarov.streetmap.persistence.jpa.entity.relation.RelationTagEntity;
import ru.nsu.stoliarov.streetmap.persistence.jpa.repository.relation.RelationMemberRepository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.repository.relation.RelationRepository;
import ru.nsu.stoliarov.streetmap.persistence.jpa.repository.relation.RelationTagRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;

    private final RelationTagRepository relationTagRepository;

    private final RelationMemberRepository relationMemberRepository;

    private final RelationEntityConverter relationConverter;

    public Relation getRelation(Long id) {

        if (id == null) {
            return null;
        }

        RelationEntity relationEntity = relationRepository.findById(id).orElse(null);

        if (relationEntity == null) {
            return null;
        }

        List<RelationTagEntity> tagEntities = relationTagRepository.findAllByRelationId(id);
        List<RelationMemberEntity> memberEntities = relationMemberRepository.findAllByRelationId(id);

        List<Pair<String, String>> tags = convertTags(tagEntities);
        List<RelationMember> members = convertMembers(memberEntities);

        return relationConverter.convert(relationEntity, members, tags);
    }

    @Transactional
    public StatusHolder saveRelation(Relation relation) {

        RelationEntity relationEntity = relationConverter.convert(relation);

        if (relationEntity == null) {
            return StatusHolder.buildFaultStatus("Failed to save relation: null");
        }

        relationRepository.save(relationEntity);
        saveMembers(relation);
        saveTags(relation);

        return StatusHolder.buildSuccessStatus();
    }

    @Transactional
    public StatusHolder deleteRelation(Long id) {

        if (id == null) {
            return StatusHolder.buildFaultStatus("Failed to delete relation with id: null");
        }

        if (relationRepository.existsById(id)) {
            relationRepository.deleteById(id);
        }

        return StatusHolder.buildSuccessStatus();
    }

    @Transactional
    public RelationUpdateResponse updateRelation(Relation relation) {

        RelationEntity relationEntity = relationConverter.convert(relation);

        if (relationEntity == null) {
            return new RelationUpdateResponse(StatusHolder.buildFaultStatus("Failed to update relation: null"));
        }

        relationRepository.save(relationEntity);

        updateMembers(relation);
        updateTags(relation);

        Relation updatedRelation = getRelation(relation.getId());

        return new RelationUpdateResponse(updatedRelation);
    }

    private void updateMembers(Relation relation) {

        Long relationId = relation.getId();

        List<RelationMemberEntity> actualMembers = relationMemberRepository.findAllByRelationId(relationId);
        List<RelationMember> newMembers = relation.getMember();

        if (!actualMembers.equals(newMembers)) {
            relationMemberRepository.deleteAllByRelationId(relationId);
            saveMembers(newMembers, relationId);
        }
    }

    private void updateTags(Relation relation) {

        Long relationId = relation.getId();

        List<RelationTagEntity> actualTagEntities = relationTagRepository.findAllByRelationId(relationId);
        List<Pair<String, String>> newTags = relation.getTags();
        List<Pair<String, String>> actualTags = convertTags(actualTagEntities);

        newTags.stream()
                .filter(newTag -> !actualTags.contains(newTag))
                .forEach(newTag -> saveTag(newTag, relationId));

        actualTagEntities.stream()
                .filter(actualTag -> !newTags.contains(convertTag(actualTag)))
                .filter(actualTag -> relationTagRepository.existsById(actualTag.getId()))
                .forEach(actualTag -> relationTagRepository.deleteById(actualTag.getId()));
    }

    private void saveTags(Relation relation) {
        saveTags(relation.getTags(), relation.getId());
    }

    private void saveTags(List<Pair<String, String>> tags, Long relationId) {

        ListUtils.emptyIfNull(tags).forEach(tag -> saveTag(tag, relationId));
    }

    private void saveTag(Pair<String, String> tag, Long relationId) {

        RelationTagEntity relationTag = new RelationTagEntity();

        relationTag.setId(RandomUtils.nextLong());
        relationTag.setKey(tag.getKey());
        relationTag.setValue(tag.getValue());
        relationTag.setRelationId(relationId);

        relationTagRepository.save(relationTag);
    }

    private void saveMembers(Relation relation) {
        saveMembers(relation.getMember(), relation.getId());
    }

    private void saveMembers(List<RelationMember> members, Long relationId) {

        for (RelationMember member : CollectionUtils.emptyIfNull(members)) {

            RelationMemberEntity memberEntity = new RelationMemberEntity();

            memberEntity.setId(RandomUtils.nextLong());
            memberEntity.setRelationId(relationId);
            memberEntity.setRole(member.getRole().getName());
            memberEntity.setType(member.getType().getName());
            memberEntity.setRef(member.getRef());

            relationMemberRepository.save(memberEntity);
        }
    }

    private List<RelationMember> convertMembers(List<RelationMemberEntity> members) {

        return CollectionUtils.emptyIfNull(members).stream()
                .filter(Objects::nonNull)
                .map(this::convertMember)
                .collect(Collectors.toList());
    }

    private RelationMember convertMember(RelationMemberEntity member) {

        RelationMemberType type = RelationMemberType.findByName(member.getType());
        RelationMemberRole role = RelationMemberRole.findByName(member.getRole());
        Long ref = member.getRef();

        return new RelationMember(type, ref, role);
    }

    private List<Pair<String, String>> convertTags(List<RelationTagEntity> tags) {

        return CollectionUtils.emptyIfNull(tags).stream()
                .filter(Objects::nonNull)
                .map(this::convertTag)
                .collect(Collectors.toList());
    }

    private Pair<String, String> convertTag(RelationTagEntity tag) {
        return Pair.of(tag.getKey(), tag.getValue());
    }
}
