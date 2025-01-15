package com.project.forde.mapper;

import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    @Mapping(source = "tag.tagId", target = "tagId")
    @Mapping(source = "tag.tagName", target = "tagName")
    TagDto.Response.TagWithoutCount toTagWithoutCount(Tag tag);

    @Mapping(source = "tag.tagId", target = "tagId")
    @Mapping(source = "tag.tagName", target = "tagName")
    @Mapping(source = "tag.tagCount", target = "tagCount")
    TagDto.Response.TagWithCount toTagWithCount(Tag tag);
}
