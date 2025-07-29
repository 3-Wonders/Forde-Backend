package com.project.forde.mapper;

import com.project.forde.dto.comment.CommentDto;
import com.project.forde.dto.mention.MentionDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.Comment;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        uses = CustomTimestampMapper.class,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "user", target = "uploader")
    @Mapping(source = "board", target = "board")
    @Mapping(source = "content", target = "content")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    Comment toEntity(AppUser user, Board board, String content);

    @Mapping(source = "comment.commentId", target = "commentId")
    @Mapping(source = "comment.parent.commentId", target = "parentId")
    @Mapping(source = "comment.uploader", target = "uploader")
    @Mapping(source = "mentions", target = "mentions")
    @Mapping(source = "comment.content", target = "content")
    @Mapping(source = "hasReply", target = "hasReply")
    @Mapping(source = "comment.isAdopt", target = "isAdopt")
    @Mapping(source = "comment.createdTime", target = "createdTime", qualifiedBy = { MapCreatedTime.class, CustomTimestampTranslator.class })
    CommentDto.Response.Comment toDto(Comment comment, List<MentionDto.Response.Mention> mentions, Boolean hasReply);

    @AfterMapping
    default void handleDeleteComment(@MappingTarget CommentDto.Response.Comment dto, Comment comment) {
        if (comment.getIsDeleted()) {
            dto.setContent("삭제된 댓글입니다.");
            dto.setUploader(null);
        }
    }
}
