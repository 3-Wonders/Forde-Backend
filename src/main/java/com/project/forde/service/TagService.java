package com.project.forde.service;

import com.project.forde.annotation.UserVerify;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Tag;
import com.project.forde.mapper.TagMapper;
import com.project.forde.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;

    public TagDto.Response.TagsWithoutCount getTags(final String keyword) {
        List<Tag> tags = tagRepository.findTop10ByTagNameStartingWith(keyword);

        return new TagDto.Response.TagsWithoutCount(
                tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList()
        );
    }

    public TagDto.Response.TagsWithCount getPopularTags(final int count) {
        Pageable pageable = Pageable.ofSize(count);
        List<Tag> tags = tagRepository.findAllByOrderByTagCountDesc(pageable);

        return new TagDto.Response.TagsWithCount(
                tags.stream().map(TagMapper.INSTANCE::toTagWithCount).toList()
        );
    }

    /**
     * 태그 아이디 리스트로 태그 리스트를 가져온다.
     *
     * @param tagIds 태그 아이디 리스트
     * @return 해당 태그 리스트
     */
    public List<Tag> getTagsWithIds(final List<Long> tagIds) {
        return tagRepository.findAllByTagIdIn(tagIds);
    }

    @Transactional
    @UserVerify
    public TagDto.Response.TagId create(final TagDto.Request request) {
        Tag tag = tagRepository.save(
                Tag.builder()
                        .tagName(request.getTagName())
                        .build()
        );
        return new TagDto.Response.TagId(tag.getTagId());
    }

    /**
     * 태그의 count 1을 증가시킨다.
     *
     * @param tagIds 태그 아이디 리스트
     * @return 태그 리스트
     */
    @Transactional
    public List<Tag> increaseTagCount(final List<Long> tagIds) {
        List<Tag> tags = tagRepository.findAllByTagIdIn(tagIds);
        tags.forEach(tag -> tag.setTagCount(tag.getTagCount() + 1));
        
        tagRepository.saveAll(tags);
        return tags;
    }
}
