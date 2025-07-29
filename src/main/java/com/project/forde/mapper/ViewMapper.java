package com.project.forde.mapper;

import com.project.forde.entity.*;
import com.project.forde.entity.composite.BoardViewPK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ViewMapper {
    ViewMapper INSTANCE = Mappers.getMapper(ViewMapper.class);

    @Mapping(source = "pk", target = "boardViewPK")
    @Mapping(target = "createdTime", ignore = true)
    BoardView toEntity(BoardViewPK pk);
}
