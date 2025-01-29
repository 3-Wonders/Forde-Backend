package com.project.forde.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.project.forde.annotation.ExtractUserId;
import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.ExtractUserIdAspect;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.activityLog.ActivityLogEventDto;
import com.project.forde.dto.board.BoardDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.*;
import com.project.forde.entity.composite.BoardTagPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.*;
import com.project.forde.repository.*;

import com.project.forde.type.BoardTypeEnum;
import com.project.forde.type.ImageActionEnum;
import com.project.forde.type.ImagePathEnum;
import com.project.forde.type.SortBoardTypeEnum;
import com.project.forde.util.CustomTimestamp;
import com.project.forde.util.FileStore;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;
    private final BoardTagRepository boardTagRepository;
    private final BoardImageRepository boardImageRepository;

    private final FileService fileService;
    private final TagService tagService;
    private final ViewService viewService;
    private final BoardTagService boardTagService;
    private final BoardImageService boardImageService;
    private final AppUserService appUserService;

    private final ApplicationEventPublisher publisher;
    private final FileStore fileStore;

    private BoardDto.Response.Boards createBoardsDto(Page<Board> boards) {
        List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_BoardIn(boards.toList());
        ListMultimap<Long, Tag> tagMap = ArrayListMultimap.create();

        boardTags.forEach(boardTag -> {
            Tag tag = boardTag.getBoardTagPK().getTag();
            tagMap.put(boardTag.getBoardTagPK().getBoard().getBoardId(), tag);
        });
  
        List<BoardDto.Response.Boards.Board> mappingBoards = boards.getContent().stream().map(board -> {
            List<Tag> tags = tagMap.get(board.getBoardId());
            List<TagDto.Response.TagWithoutCount> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

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

    @ExtractUserId
    public BoardDto.Response.Boards getSearchPosts(final int page, final int count, final String keyword) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Board> boards = boardRepository.findALlByTitleContainingOrderByCreatedTimeDesc(pageable, keyword);

        Long userId = ExtractUserIdAspect.getUserId();

        if (userId != null) {
            publisher.publishEvent(new ActivityLogEventDto.Create.Search(
                appUserService.getUser(userId),
                keyword
            ));
        }

        return createBoardsDto(boards);
    }

    @ExtractUserId
    public BoardDto.Response.Boards getFollowingNews(final int page, final int count, @Nullable final Character type) {
        Long userId = ExtractUserIdAspect.getUserId();
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<Board> boards = boardRepository.findAllByCategoryAndFollowingOrderByBoardIdDesc(pageable, userId, type);
        return createBoardsDto(boards);
    }

    public BoardDto.Response.Boards getPostsWithTag(final String keyword, final int page, final int count) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Board> boards = boardRepository.findAllByTagNameOrderByCreatedTimeDesc(pageable, keyword);

        return createBoardsDto(boards);
    }

    @ExtractUserId
    public BoardDto.Response.Detail getPost(final Long boardId) {
        Board board = boardRepository.findBoardIncludeUploaderByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_Board(board);
        List<Tag> tags = boardTags.stream().map(tag -> tag.getBoardTagPK().getTag()).toList();
        List<TagDto.Response.TagWithoutCount> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

        Long userId = ExtractUserIdAspect.getUserId();

        if (userId != null) {
            // TODO : userId가 존재한다면 (로그인 상태라면) 조회수 증가
            // viewService.createView(userId, boardId);

            publisher.publishEvent(new ActivityLogEventDto.Create.Revisit(
                appUserService.getUser(userId),
                board
            ));
        }

        return BoardMapper.INSTANCE.toDetail(board, responseTags);
    }

    @UserVerify
    public BoardDto.Response.Update getUpdatePost(final Long boardId) {
        Long userId = UserVerifyAspect.getUserId();
        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        if (!board.getUploader().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_BOARD_UPLOADER);
        }

        List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_Board(board);
        List<Tag> tags = boardTags.stream().map(tag -> tag.getBoardTagPK().getTag()).toList();
        List<TagDto.Response.TagWithoutCount> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

        List<BoardImage> boardImages = boardImageRepository.findAllByBoard(board);
        List<Long> imageIds = boardImages.stream().map(BoardImage::getImageId).toList();

        return BoardMapper.INSTANCE.toUpdatePost(board, responseTags, imageIds);
    }

    public BoardDto.Response.Boards getDailyNews(final int page, final int count) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Board> boards = boardRepository.findAllByDailyNews(pageable);

        return createBoardsDto(boards);
    }

    public BoardDto.Response.Boards getMonthlyNews(final int page, final int count) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Board> boards = boardRepository.findAllByMonthlyNews(pageable);

        return createBoardsDto(boards);
    }

    @Transactional
    @UserVerify
    public Long create(final BoardDto.Request.Create request) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        if (user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        Board board = BoardMapper.INSTANCE.toEntity(user, request, null);
        Board createdBoard = boardRepository.save(board);

        List<Tag> tags = tagService.increaseTagCount(request.getTagIds());
        boardTagService.createBoardTag(createdBoard, tags);
        boardImageService.createImages(createdBoard, request.getImageIds());

        appUserService.increaseCount(user, BoardTypeEnum.valueOf(request.getBoardType()));

        fileService.processThumbnailAndSave(
                request.getThumbnail(),
                ImagePathEnum.BOARD.getPath(),
                createdBoard,
                boardRepository::save
        );

        return createdBoard.getBoardId();
    }

    @Transactional
    @UserVerify
    public void update(final Long boardId, final BoardDto.Request.Update request) {
        Long userId = UserVerifyAspect.getUserId();
        appUserService.getUser(userId);

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        String oldThumbnailPath = board.getThumbnailPath();

        if (!board.getUploader().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_BOARD_UPLOADER);
        }

        updateTags(board, request.getTagIds());
        boardImageService.updateDiffImages(board, request.getImageIds());

        board.setCategory(request.getBoardType().charAt(0));
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setUpdatedTime(new CustomTimestamp().getTimestamp());
        boardRepository.save(board);

        if (request.getThumbnailAction().equals(ImageActionEnum.UPLOAD.getType())) {
            fileService.processThumbnailAndSave(
                    request.getThumbnail(),
                    ImagePathEnum.BOARD.getPath(),
                    board,
                    boardRepository::save
            );

            // TODO: Kafka 또는 무언가를 사용하여 Topic을 발생시키고, 삭제할 이미지를 모아서 삭제
            if (oldThumbnailPath != null) {
                fileStore.deleteFile(oldThumbnailPath);
            }
        } else if (request.getThumbnailAction().equals(ImageActionEnum.DELETE.getType())) {
            board.setThumbnailPath(null);
            board.setThumbnailSize(null);
            board.setThumbnailType(null);

            // TODO: Kafka 또는 무언가를 사용하여 Topic을 발생시키고, 삭제할 이미지를 모아서 삭제
            if (oldThumbnailPath != null) {
                fileStore.deleteFile(oldThumbnailPath);
            }
        }

        boardRepository.save(board);
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

    @Transactional
    @UserVerify
    public void delete(final Long boardId) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        if (!board.getUploader().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_BOARD_UPLOADER);
        }

        List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_Board(board);
        List<Tag> tags = boardTags.stream().map(tag -> tag.getBoardTagPK().getTag()).toList();

        tags.forEach(tag -> tag.setTagCount(tag.getTagCount() - 1));

        tagRepository.saveAll(tags);
        boardTagRepository.deleteAll(boardTags);
        boardRepository.delete(board);

        List<BoardImage> boardImages = boardImageRepository.findAllByBoard(board);
        List<String> imagePaths = new ArrayList<>(boardImages.stream().map(BoardImage::getImagePath).toList());
        boardImageRepository.deleteAll(boardImages);

        appUserService.decreaseCount(user, BoardTypeEnum.valueOf(String.valueOf(board.getCategory())));

        if (board.getThumbnailPath() != null) {
            imagePaths.add(board.getThumbnailPath());
        }

        imagePaths.forEach(fileStore::deleteFile);
    }
}
