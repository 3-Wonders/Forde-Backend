package com.project.forde.mapper;

import com.project.forde.entity.DraftTag;
import com.project.forde.entity.composite.DraftTagPK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DraftTagMapper {
    DraftTagMapper INSTANCE = Mappers.getMapper(DraftTagMapper.class);

    @Mapping(source = "pk", target = "draftTagPK")
    DraftTag toEntity(DraftTagPK pk);
}
