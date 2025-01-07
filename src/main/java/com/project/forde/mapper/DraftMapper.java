package com.project.forde.mapper;

import com.project.forde.dto.draft.DraftDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.Draft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CustomTimestampMapper.class)
public interface DraftMapper {
    DraftMapper INSTANCE = Mappers.getMapper(DraftMapper.class);

    @Mapping(source = "draft.draftId", target = "draftId")
    @Mapping(source = "draft.category", target = "boardType")
    @Mapping(source = "draft.title", target = "title")
    @Mapping(source = "draft.content", target = "content")
    @Mapping(source = "draft.thumbnailPath", target = "thumbnail")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "imageIds", target = "imageIds")
    @Mapping(source = "draft.createdTime", target = "createdTime", qualifiedBy = { MapCreatedTime.class, CustomTimestampTranslator.class })
    DraftDto.Response.Draft toDraft(Draft draft, List<TagDto.Response.Tag> tags, List<Long> imageIds);
}
