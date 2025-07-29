package com.project.forde.mapper;

import com.project.forde.entity.BoardLike;
import com.project.forde.entity.composite.BoardLikePK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LikeMapper {
    LikeMapper INSTANCE = Mappers.getMapper(LikeMapper.class);

    @Mapping(source = "pk", target = "boardLikePK")
    @Mapping(target = "createdTime", ignore = true)
    BoardLike toEntity(BoardLikePK pk);
}
