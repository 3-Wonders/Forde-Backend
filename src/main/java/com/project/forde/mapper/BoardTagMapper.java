package com.project.forde.mapper;

import com.project.forde.entity.Board;
import com.project.forde.entity.BoardTag;
import com.project.forde.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardTagMapper {
    BoardTagMapper INSTANCE = Mappers.getMapper(BoardTagMapper.class);

    @Mapping(source = "board", target = "board")
    @Mapping(source = "tag", target = "tag")
    BoardTag toEntity(Board board, Tag tag);
}
