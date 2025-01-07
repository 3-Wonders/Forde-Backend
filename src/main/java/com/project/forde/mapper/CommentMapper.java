package com.project.forde.mapper;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.BoardLike;
import com.project.forde.entity.Comment;
import com.project.forde.entity.composite.BoardLikePK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "user", target = "uploader")
    @Mapping(source = "board", target = "board")
    @Mapping(source = "content", target = "content")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    Comment toEntity(AppUser user, Board board, String content);
}
