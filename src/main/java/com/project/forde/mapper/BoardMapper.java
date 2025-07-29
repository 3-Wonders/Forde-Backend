package com.project.forde.mapper;

import com.project.forde.dto.FileDto;
import com.project.forde.dto.board.BoardDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = { CustomTimestampMapper.class, FileUrlResolver.class })
public interface BoardMapper {
    @Mapping(source = "user", target = "uploader")
    @Mapping(source = "request.boardType", target = "category")
    @Mapping(source = "request.title", target = "title")
    @Mapping(source = "request.content", target = "content")
    @Mapping(source = "file.storePath", target = "thumbnailPath")
    @Mapping(source = "file.extension", target = "thumbnailType")
    @Mapping(source = "file.size", target = "thumbnailSize")
    @Mapping(target = "viewCount", constant = "0")
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "commentCount", constant = "0")
    Board toEntity(AppUser user, BoardDto.Request.Create request, FileDto file);

    @Mapping(source = "board.boardId", target = "boardId")
    @Mapping(source = "board.thumbnailPath", target = "thumbnail", qualifiedByName = "getThumbnailPath")
    @Mapping(source = "board.title", target = "title")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "board.isLike", target = "isLike")
    @Mapping(source = "board.uploader.userId", target = "uploader.userId")
    @Mapping(source = "board.uploader.email", target = "uploader.email")
    @Mapping(source = "board.uploader.nickname", target = "uploader.nickname")
    @Mapping(source = "board.uploader.profilePath", target = "uploader.profilePath", qualifiedByName = "getProfilePath")
    @Mapping(source = "board.viewCount", target = "viewCount")
    @Mapping(source = "board.likeCount", target = "likeCount")
    @Mapping(source = "board.commentCount", target = "commentCount")
    @Mapping(source = "board.createdTime", target = "createdTime", qualifiedBy = { MapCreatedTime.class, CustomTimestampTranslator.class })
    BoardDto.Response.Boards.Board toBoardsInBoard(Board board, List<TagDto.Response.TagWithoutCount> tags);

    @Mapping(source = "board.boardId", target = "boardId")
    @Mapping(source = "board.category", target = "boardType")
    @Mapping(source = "board.uploader.userId", target = "uploader.userId")
    @Mapping(source = "board.uploader.email", target = "uploader.email")
    @Mapping(source = "board.uploader.nickname", target = "uploader.nickname")
    @Mapping(source = "board.uploader.profilePath", target = "uploader.profilePath", qualifiedByName = "getProfilePath")
    @Mapping(source = "board.title", target = "title")
    @Mapping(source = "board.content", target = "content")
    @Mapping(source = "board.thumbnailPath", target = "thumbnail", qualifiedByName = "getThumbnailPath")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "board.isLike", target = "isLike")
    @Mapping(source = "board.viewCount", target = "viewCount")
    @Mapping(source = "board.likeCount", target = "likeCount")
    @Mapping(source = "board.commentCount", target = "commentCount")
    @Mapping(source = "board.createdTime", target = "createdTime", qualifiedBy = { MapCreatedTime.class, CustomTimestampTranslator.class })
    BoardDto.Response.Detail toDetail(Board board, List<TagDto.Response.TagWithoutCount> tags);

    @Mapping(source = "board.boardId", target = "boardId")
    @Mapping(source = "board.category", target = "boardType")
    @Mapping(source = "board.title", target = "title")
    @Mapping(source = "board.content", target = "content")
    @Mapping(source = "board.thumbnailPath", target = "thumbnail", qualifiedByName = "getThumbnailPath")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "imageIds", target = "imageIds")
    @Mapping(source = "board.createdTime", target = "createdTime", qualifiedBy = { MapCreatedTime.class, CustomTimestampTranslator.class })
    BoardDto.Response.Update toUpdatePost(Board board, List<TagDto.Response.TagWithoutCount> tags, List<Long> imageIds);

    @Mapping(source = "board.boardId", target = "boardId")
    @Mapping(source = "board.category", target = "boardType")
    @Mapping(source = "board.uploader.nickname", target = "uploader.nickname")
    @Mapping(source = "board.uploader.profilePath", target = "uploader.profilePath", qualifiedByName = "getProfilePath")
    @Mapping(source = "board.title", target = "title")
    @Mapping(source = "board.thumbnailPath", target = "thumbnail", qualifiedByName = "getThumbnailPath")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "board.isLike", target = "isLike")
    @Mapping(source = "board.viewCount", target = "viewCount")
    @Mapping(source = "board.likeCount", target = "likeCount")
    @Mapping(source = "board.commentCount", target = "commentCount")
    @Mapping(source = "board.createdTime", target = "createdTime", qualifiedBy = { MapCreatedTime.class, CustomTimestampTranslator.class })
    BoardDto.Response.UserBoards.UserBoard toUserBoardsInBoard(Board board, List<TagDto.Response.TagWithoutCount> tags);

    @Mapping(source = "board.boardId", target = "boardId")
    @Mapping(source = "board.category", target = "boardType")
    @Mapping(source = "board.uploader.nickname", target = "uploader.nickname")
    @Mapping(source = "board.uploader.profilePath", target = "uploader.profilePath", qualifiedByName = "getProfilePath")
    @Mapping(source = "board.title", target = "title")
    @Mapping(source = "board.thumbnailPath", target = "thumbnail", qualifiedByName = "getThumbnailPath")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "board.isLike", target = "isLike")
    @Mapping(source = "board.viewCount", target = "viewCount")
    @Mapping(source = "board.likeCount", target = "likeCount")
    @Mapping(source = "board.commentCount", target = "commentCount")
    @Mapping(source = "comment.content", target = "comment")
    @Mapping(source = "board.createdTime", target = "createdTime", qualifiedBy = { MapCreatedTime.class, CustomTimestampTranslator.class })
    BoardDto.Response.UserComments.UserComment toUserCommentInBoards(Board board, List<TagDto.Response.TagWithoutCount> tags, Comment comment);

    @Mapping(source = "boardId", target = "boardId")
    @Mapping(source = "thumbnail", target = "thumbnail", qualifiedByName = "getThumbnailPath")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "nickname", target = "nickname")
    BoardDto.Response.IntroPost.Item toIntroPostItem(Long boardId, String thumbnail, String title, String nickname);
}
