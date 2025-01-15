package com.project.forde.service;

import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Tag;
import com.project.forde.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;

    private final AppUserService appUserService;

    public List<Tag> getTags(final List<Long> tagIds) {
        return tagRepository.findAllByTagIdIn(tagIds);
    }

    @Transactional
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
