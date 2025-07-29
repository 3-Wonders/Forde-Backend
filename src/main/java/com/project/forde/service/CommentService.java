package com.project.forde.service;

import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.comment.CommentDto;
import com.project.forde.dto.mention.MentionDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.Comment;
import com.project.forde.entity.Mention;
import com.project.forde.entity.composite.MentionPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.CommentMapper;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.BoardRepository;
import com.project.forde.repository.CommentRepository;
import com.project.forde.repository.MentionRepository;
import com.project.forde.type.AppUserCount;
import com.project.forde.type.BoardTypeEnum;
import com.project.forde.type.NotificationTypeEnum;
import com.project.forde.util.CustomTimestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final AppUserRepository appUserRepository;
    private final BoardRepository boardRepository;
    private final MentionRepository mentionRepository;

    private final MentionService mentionService;
    private final NotificationService notificationService;
    private final AppUserService appUserService;

    public CommentDto.Response.Comments getComments(final Long boardId, final int page, final int count) {
        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Comment> response = commentRepository.findAllByBoardAndParentIsNullOrderByCommentIdDesc(board, pageable);

        return getComments(response.getContent());
    }

    public CommentDto.Response.Comments getReplies(final Long boardId, final Long parentId, final int page, final int count) {
        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Comment> response = commentRepository.findAllByParent_CommentIdOrderByCommentIdDesc(parentId, pageable);

        return getComments(response.getContent());
    }

    @Transactional
    @UserVerify
    public void create(final Long boardId, final CommentDto.Request request) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Comment createdComment = createComment(user, board, null, request);

        appUserService.increaseCount(user, AppUserCount.COMMENT_COUNT);
        board.setCommentCount(board.getCommentCount() + 1);
        boardRepository.save(board);

        mentionService.create(
                userId,
                createdComment,
                request
        );

        notificationService.sendNotification(
                user,
                board.getUploader(),
                NotificationTypeEnum.BOARD_COMMENT,
                board,
                createdComment
        );
    }

    @Transactional
    @UserVerify
    public void createReply(final Long boardId, final Long parentId, final CommentDto.Request request) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Comment parentComment = commentRepository.findByCommentId(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));

        Comment createdComment = createComment(user, board, parentComment, request);

        appUserService.increaseCount(user, AppUserCount.COMMENT_COUNT);
        board.setCommentCount(board.getCommentCount() + 1);
        boardRepository.save(board);

        mentionService.create(
                userId,
                createdComment,
                request
        );

        notificationService.sendNotification(
                user,
                board.getUploader(),
                NotificationTypeEnum.BOARD_COMMENT,
                board,
                createdComment
        );

        notificationService.sendNotification(
                user,
                parentComment.getUploader(),
                NotificationTypeEnum.COMMENT_REPLY,
                board,
                createdComment
        );
    }

    @Transactional
    @UserVerify
    public void adopt(final Long boardId, final Long commentId) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Boolean existAdoptedComment = commentRepository.existsByBoardAndIsAdopt(board, true);
        if (existAdoptedComment) {
            throw new CustomException(ErrorCode.ALREADY_ADOPTED_COMMENT);
        }

        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));

        if (comment.getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_COMMENT);
        } else if (!comment.getBoard().getBoardId().equals(board.getBoardId())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_COMMENT_BOARD);
        } else if (!comment.getBoard().getCategory().equals(BoardTypeEnum.Q.getType())) {
            throw new CustomException(ErrorCode.NOT_QUESTION_BOARD);
        } else if (!comment.getBoard().getUploader().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_BOARD_UPLOADER);
        }

        comment.setIsAdopt(true);
        commentRepository.save(comment);
    }

    @Transactional
    @UserVerify
    public void update(final Long boardId, final Long commentId, final CommentDto.Request request) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));

        if (comment.getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_COMMENT);
        } else if (!comment.getUploader().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_COMMENT_UPLOADER);
        } else if (!comment.getBoard().getBoardId().equals(board.getBoardId())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_COMMENT_BOARD);
        }

        comment.setContent(request.getContent());
        comment.setUpdatedTime(new CustomTimestamp().getTimestamp());

        List<Long> mentionIds = request.getUserIds();

        if (mentionIds != null && !mentionIds.isEmpty()) {
            List<Mention> existingMentions = mentionRepository.findAllByMentionPK_Comment(comment);
            List<Mention> deleteMentions = existingMentions.stream()
                    .filter(mention -> !mentionIds.contains(mention.getMentionPK().getUser().getUserId()))
                    .toList();

            Set<Long> existingMentionUserIds = existingMentions.stream()
                    .map(mention -> mention.getMentionPK().getUser().getUserId())
                    .collect(Collectors.toSet());
            Set<Long> newMentionUserIds = mentionIds.stream()
                    .filter(uid -> !existingMentionUserIds.contains(uid) && !uid.equals(userId))
                    .collect(Collectors.toSet());
            List<AppUser> newMentionUsers = appUserRepository.findAllByUserIdIn(newMentionUserIds.stream().toList());
            List<Mention> newMentions = newMentionUsers.stream()
                    .map(newUser -> {
                        MentionPK mentionPK = new MentionPK(comment, newUser);
                        Mention mention = new Mention();
                        mention.setMentionPK(mentionPK);

                        return mention;
                    })
                    .toList();

            if (mentionService.isInValidMention(newMentionUsers, request.getContent())) {
                throw new CustomException(ErrorCode.INVALID_MENTION);
            }

            mentionRepository.deleteAllInBatch(deleteMentions);
            mentionRepository.saveAll(newMentions);

            // TODO: Notification 발생
        }
    }

    @Transactional
    @UserVerify
    public void delete(final Long boardId, final Long commentId) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));

        if (comment.getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_COMMENT);
        } else if (!comment.getUploader().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_COMMENT_UPLOADER);
        } else if (!comment.getBoard().getBoardId().equals(board.getBoardId())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_COMMENT_BOARD);
        } else if (comment.getIsAdopt() != null && comment.getIsAdopt()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ALREADY_ADOPTED);
        }

        appUserService.decreaseCount(user, AppUserCount.COMMENT_COUNT);

        board.setCommentCount(board.getCommentCount() - 1);
        boardRepository.save(board);

        comment.setIsDeleted(true);
        comment.setDeletedTime(new CustomTimestamp().getTimestamp());
        commentRepository.save(comment);
    }

    private CommentDto.Response.Comments getComments(final List<Comment> comments) {
        long total = comments.size();

        List<Mention> mentions = mentionService.getMentionIn(comments);

        List<CommentDto.Response.Comment> commentList = comments.stream().map(
                comment -> CommentMapper.INSTANCE.toDto(
                        comment,
                        mentions.stream().filter(
                                mention -> mention.getMentionPK().getComment().equals(comment)
                        ).map(
                                mention -> new MentionDto.Response.Mention(
                                        mention.getMentionPK().getUser().getUserId(),
                                        mention.getMentionPK().getUser().getNickname()
                                )
                        ).toList(),
                        comment.getHasReply()
                )
        ).toList();

        return new CommentDto.Response.Comments(commentList, total);
    }

    private Comment createComment(final AppUser user, final Board board, final Comment parent, final CommentDto.Request request) {
        Comment comment = CommentMapper.INSTANCE.toEntity(user, board, request.getContent());
        if (board.getCategory().equals(BoardTypeEnum.Q.getType())) {
            comment.setIsAdopt(false);
        }

        if (parent != null) {
            comment.setParent(parent);
        }

        return commentRepository.save(comment);
    }
}
