package com.project.forde.mapper;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.BoardLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LikeMapper {
    LikeMapper INSTANCE = Mappers.getMapper(LikeMapper.class);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "board", target = "board")
    @Mapping(target = "createdTime", ignore = true)
    BoardLike toEntity(AppUser user, Board board);
}
