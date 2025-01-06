package com.project.forde.service;

import com.project.forde.entity.*;
import com.project.forde.entity.composite.DraftTagPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.DraftTagMapper;
import com.project.forde.repository.DraftRepository;
import com.project.forde.repository.DraftTagRepository;
import com.project.forde.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DraftTagService {
    private final TagRepository tagRepository;
    private final DraftTagRepository draftTagRepository;

    /**
     * 임시 저장에 태그를 추가하는 메서드
     *
     * @param draft 임시 저장
     * @param tags  태그 리스트
     */
    @Transactional
    public void createDraftTag(final Draft draft, final List<Tag> tags) {
        List<DraftTag> draftTags =  tags.stream().map(tag ->
                DraftTagMapper.INSTANCE.toEntity(new DraftTagPK(draft, tag))
        ).toList();

        if (draftTags.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TAG);
        }

        draftTagRepository.saveAll(draftTags);
    }

    /**
     * 기존 태그와 새로운 태그를 비교하여 태그를 업데이트하는 메서드
     *
     * @param draft      게시글
     * @param newTagIds  새로운 태그 아이디 리스트
     */
    @Transactional
    public void updateDiffTags(final Draft draft, final List<Long> newTagIds) {
        List<Tag> newTags = tagRepository.findAllByTagIdIn(newTagIds);
        if (newTags.size() != newTagIds.size() || newTags.isEmpty() || newTags.size() > 3) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TAG);
        }

        List<DraftTag> draftTags = draftTagRepository.findAllByDraftTagPK_Draft(draft);
        List<Tag> existingTags = draftTags.stream().map(
                draftTag -> draftTag.getDraftTagPK().getTag()
        ).toList();

        Set<Long> existingTagIds = existingTags.stream().map(Tag::getTagId).collect(Collectors.toSet());
        Set<Long> newTagIdSet = new HashSet<>(newTagIds);

        List<DraftTag> deleteTags = draftTags.stream().filter(
                draftTag -> !newTagIdSet.contains(draftTag.getDraftTagPK().getTag().getTagId())
        ).toList();

        List<DraftTag> addTags = newTags.stream().filter(
                tag -> !existingTagIds.contains(tag.getTagId())
        ).map(
                tag -> DraftTagMapper.INSTANCE.toEntity(new DraftTagPK(draft, tag))
        ).toList();

        draftTagRepository.deleteAllInBatch(deleteTags);
        draftTagRepository.saveAll(addTags);
    }
}
