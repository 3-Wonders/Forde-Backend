package com.project.forde.service;

import com.project.forde.entity.*;
import com.project.forde.entity.composite.DraftTagPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.DraftTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DraftTagService {
    public List<DraftTag> createDraftTag(final Draft draft, final List<Tag> tags) {
        List<DraftTag> draftTags =  tags.stream().map(tag ->
                DraftTagMapper.INSTANCE.toEntity(new DraftTagPK(draft, tag))
        ).toList();

        if (draftTags.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TAG);
        }

        return draftTags;
    }
}
