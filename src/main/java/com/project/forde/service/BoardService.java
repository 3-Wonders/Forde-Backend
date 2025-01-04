package com.project.forde.service;

import com.project.forde.dto.FileDto;
import com.project.forde.dto.board.BoardDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.*;
import com.project.forde.entity.composite.BoardTagPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.exception.FileUploadException;
import com.project.forde.mapper.*;
import com.project.forde.repository.*;

import com.project.forde.type.ImagePathEnum;
import com.project.forde.type.SortBoardTypeEnum;
import com.project.forde.util.FileStore;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final AppUserRepository appUserRepository;
    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;
    private final BoardTagRepository boardTagRepository;
    private final BoardImageRepository boardImageRepository;

    private final FileStore fileStore;

    private BoardDto.Response.Boards createBoardsDto(Page<Board> boards) {
        List<BoardDto.Response.Boards.Board> mappingBoards = boards.getContent().stream().map(board -> {
            List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_Board(board);
            List<Tag> tags = boardTags.stream().map(tag -> tag.getBoardTagPK().getTag()).toList();
            List<TagDto.Response.Tag> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

            return BoardMapper.INSTANCE.toBoardsInBoard(board, responseTags);
        }).toList();

        return new BoardDto.Response.Boards(mappingBoards, boards.getTotalElements());
    }

    public BoardDto.Response.Boards getRecentPosts(final int page, final int count, final SortBoardTypeEnum type) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Board> boards = null;

        if (type == SortBoardTypeEnum.ALL) {
            boards = boardRepository.findAllByOrderByCreatedTimeDesc(pageable);
        } else {
            boards = boardRepository.findAllByCategoryOrderByCreatedTimeDesc(pageable, type.getType());
        }

        return createBoardsDto(boards);
    }

    public BoardDto.Response.Boards getSearchPosts(final int page, final int count, final String keyword) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Board> boards = boardRepository.findALlByTitleContainingOrderByCreatedTimeDesc(pageable, keyword);

        return createBoardsDto(boards);
    }

    public BoardDto.Response.Detail getPost(final Long boardId) {
        Board board = boardRepository.findBoardIncludeUploaderByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_Board(board);
        List<Tag> tags = boardTags.stream().map(tag -> tag.getBoardTagPK().getTag()).toList();
        List<TagDto.Response.Tag> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

        return BoardMapper.INSTANCE.toDetail(board, responseTags);
    }

    public BoardDto.Response.Update getUpdatePost(final Long userId, final Long boardId) {
        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        if (!board.getUploader().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_BOARD_UPLOADER);
        }

        List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_Board(board);
        List<Tag> tags = boardTags.stream().map(tag -> tag.getBoardTagPK().getTag()).toList();
        List<TagDto.Response.Tag> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

        List<BoardImage> boardImages = boardImageRepository.findAllByBoard(board);
        List<Long> imageIds = boardImages.stream().map(BoardImage::getImageId).toList();

        return BoardMapper.INSTANCE.toUpdatePost(board, responseTags, imageIds);
    }

    @Transactional
    public Long create(final Long userId, final BoardDto.Request request) {
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        Board board = BoardMapper.INSTANCE.toEntity(user, request, null);
        Board createdBoard = boardRepository.save(board);

        List<Tag> tags = tagRepository.findAllByTagIdIn(request.getTagIds());
        if (tags.size() != request.getTagIds().size() || tags.isEmpty() || tags.size() > 3) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TAG);
        }
        tags.forEach(tag -> tag.setTagCount(tag.getTagCount() + 1));

        List<BoardTag> boardTags = tags.stream().map(tag ->
            BoardTagMapper.INSTANCE.toEntity(new BoardTagPK(createdBoard, tag))
        ).toList();
        if (boardTags.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TAG);
        }

        boardTagRepository.saveAll(boardTags);

        if (request.getImageIds() != null) {
            List<BoardImage> dummyImages = boardImageRepository.findAllByImageIdIn(request.getImageIds());

            if (dummyImages.size() != request.getImageIds().size()) {
                throw new CustomException(ErrorCode.BAD_REQUEST_IMAGE);
            }

            for (BoardImage image : dummyImages) {
                image.setBoard(createdBoard);
            }

            boardImageRepository.saveAll(dummyImages);
        }

        FileDto file = null;

        try {
            if (request.getThumbnail() != null) {
                file = fileStore.storeFile(ImagePathEnum.BOARD.getPath(), request.getThumbnail());
                createdBoard.setThumbnailPath(file.getStorePath());
                createdBoard.setThumbnailSize(file.getSize());
                createdBoard.setThumbnailType(file.getExtension());
            }

            boardRepository.save(createdBoard);
        } catch (Exception e) {
            if (file != null) {
                throw new FileUploadException(file.getStorePath());
            }
        }

        return createdBoard.getBoardId();
    }

    @Transactional
    public void update(final Long userId, final Long boardId, final BoardDto.Request request) {
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        String oldBoardThumbnailPath = board.getThumbnailPath();

        if (!board.getUploader().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_BOARD_UPLOADER);
        }

        updateTags(board, request.getTagIds());
        updateImages(board, request.getImageIds());

        FileDto file = null;

        try {
            if (request.getThumbnail() != null) {
                file = fileStore.storeFile(ImagePathEnum.BOARD.getPath(), request.getThumbnail());
                board.setThumbnailPath(file.getStorePath());
                board.setThumbnailSize(file.getSize());
                board.setThumbnailType(file.getExtension());
            } else {
                board.setThumbnailPath(null);
                board.setThumbnailSize(null);
                board.setThumbnailType(null);
            }

            board.setTitle(request.getTitle());
            board.setContent(request.getContent());
            boardRepository.save(board);
        } catch (Exception e) {
            if (file != null) {
                throw new FileUploadException(file.getStorePath());
            }
        }

        // TODO: Kafka 또는 무언가를 사용하여 Topic을 발생시키고, 삭제할 이미지를 모아서 삭제하도록 요청 (fileStore.deleteFile())
        if (oldBoardThumbnailPath != null) {
            fileStore.deleteFile(oldBoardThumbnailPath);
        }
    }

    private void updateTags(final Board board, final List<Long> newTagIds) {
        List<Tag> newTags = tagRepository.findAllByTagIdIn(newTagIds);
        if (newTags.size() != newTagIds.size() || newTags.isEmpty() || newTags.size() > 3) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TAG);
        }

        // 원래 있던 태그들의 카운트를 감소시키고, 새로운 태그들의 카운트를 증가시킨다.
        List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_Board(board);
        List<Tag> existingTags = boardTags.stream().map(
                boardTag -> boardTag.getBoardTagPK().getTag()
        ).toList();

        Set<Long> existingTagIdSet = existingTags.stream().map(Tag::getTagId).collect(Collectors.toSet());
        Set<Long> newTagIdSet = new HashSet<>(newTagIds);

        // 새로운 태그 ID와 기존 태그 ID를 비교하여, 삭제할 태그를 찾아서 삭제한다.
        List<BoardTag> deleteBoardTags = boardTags.stream().filter(
                boardTag -> !newTagIdSet.contains(boardTag.getBoardTagPK().getTag().getTagId())
        ).toList();

        List<Tag> decreaseTags = deleteBoardTags.stream().map(
                boardTag -> boardTag.getBoardTagPK().getTag()
        ).toList();

        List<Tag> increaseTags = newTags.stream().filter(
                tag -> !existingTagIdSet.contains(tag.getTagId())
        ).toList();

        List<BoardTag> addBoardTags = increaseTags.stream().map(tag ->
                BoardTagMapper.INSTANCE.toEntity(new BoardTagPK(board, tag))
        ).toList();

        decreaseTags.forEach(tag -> tag.setTagCount(tag.getTagCount() - 1));
        increaseTags.forEach(tag -> tag.setTagCount(tag.getTagCount() + 1));

        List<Tag> allTags = new ArrayList<>();
        allTags.addAll(decreaseTags);
        allTags.addAll(increaseTags);

        tagRepository.saveAll(allTags);

        boardTagRepository.deleteAllInBatch(deleteBoardTags);
        boardTagRepository.saveAll(addBoardTags);
    }

    private void updateImages(final Board board, final List<Long> newImageIds) {
        List<BoardImage> boardImages = boardImageRepository.findAllByBoard(board);
        Set<Long> existingIdSet = boardImages.stream().map(BoardImage::getImageId).collect(Collectors.toSet());
        Set<Long> newIdSet = new HashSet<>(newImageIds);

        // 새로운 이미지 ID와 기존 이미지 ID를 비교하여, 삭제할 이미지를 찾아서 삭제한다.
        List<BoardImage> deleteImages = boardImages.stream().filter(
                boardImage -> !newIdSet.contains(boardImage.getImageId())
        ).toList();

        if (!deleteImages.isEmpty()) {
            boardImageRepository.deleteAllInBatch(deleteImages);
        }

        // 새로운 이미지 ID와 기존 이미지 ID를 비교하여, 추가할 이미지를 찾아서 추가한다.
        HashSet<Long> diffSet = new HashSet<>(newIdSet);
        diffSet.removeAll(existingIdSet);

        if (!diffSet.isEmpty()) {
            List<BoardImage> dummyImages = boardImageRepository.findAllByImageIdIn(diffSet.stream().toList());

            for (BoardImage image : dummyImages) {
                image.setBoard(board);
            }

            boardImageRepository.saveAll(dummyImages);
        }

        // TODO: Kafka 또는 무언가를 사용하여 Topic을 발생시키고, 삭제할 이미지를 모아서 삭제하도록 요청 (fileStore.deleteFile())
        deleteImages.forEach(boardImage -> fileStore.deleteFile(boardImage.getImagePath()));
    }
}
