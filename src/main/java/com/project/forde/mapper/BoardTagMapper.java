package com.project.forde.mapper;

import com.project.forde.entity.BoardTag;
import com.project.forde.entity.composite.BoardTagPK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardTagMapper {
    BoardTagMapper INSTANCE = Mappers.getMapper(BoardTagMapper.class);

    @Mapping(source = "pk", target = "boardTagPK")
    BoardTag toEntity(BoardTagPK pk);
}
