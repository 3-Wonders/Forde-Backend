package com.project.forde.service;

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

    public List<Tag> increaseTagCount(final List<Long> tagIds) {
        List<Tag> tags = tagRepository.findAllByTagIdIn(tagIds);
        tags.forEach(tag -> tag.setTagCount(tag.getTagCount() + 1));
        return tags;
    }
}
