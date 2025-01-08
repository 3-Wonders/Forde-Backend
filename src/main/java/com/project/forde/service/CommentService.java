package com.project.forde.service;

import com.project.forde.dto.comment.CommentDto;
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
import com.project.forde.util.CustomTimestamp;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void create(final Long userId, final Long boardId, final CommentDto.Request request) {
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Comment comment = CommentMapper.INSTANCE.toEntity(user, board, request.getContent());
        Comment createdComment = commentRepository.save(comment);

        if (request.getUserId() != null && !request.getUserId().isEmpty()) {
            List<Long> withOutMeUserIds = request.getUserId().stream()
                    .filter(uid -> !uid.equals(userId))
                    .toList();
            List<AppUser> users = appUserRepository.findAllByUserIdIn(withOutMeUserIds);

            if (isInValidMention(users, request.getContent())) {
                throw new CustomException(ErrorCode.INVALID_MENTION);
            }

            // TODO: Notification 발생

            List<Mention> mentions = users.stream().map(mentionUser -> {
                MentionPK mentionPK = new MentionPK(createdComment, mentionUser);
                Mention mention = new Mention();
                mention.setMentionPK(mentionPK);

                return mention;
            }).toList();

            mentionRepository.saveAll(mentions);
        }
    }

    @Transactional
    public void update(final Long userId, final Long commentId, final CommentDto.Request request) {
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));

        comment.setContent(request.getContent());
        comment.setUpdatedTime(new CustomTimestamp().getTimestamp());

        if (!comment.getUploader().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_COMMENT_UPLOADER);
        }

        if (request.getUserId() != null && !request.getUserId().isEmpty()) {
            List<Mention> existingMentions = mentionRepository.findAllByMentionPK_Comment(comment);
            List<Mention> deleteMentions = existingMentions.stream()
                    .filter(mention -> !request.getUserId().contains(mention.getMentionPK().getUser().getUserId()))
                    .toList();

            Set<Long> existingMentionUserIds = existingMentions.stream()
                    .map(mention -> mention.getMentionPK().getUser().getUserId())
                    .collect(Collectors.toSet());
            Set<Long> newMentionUserIds = request.getUserId().stream()
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

            if (isInValidMention(newMentionUsers, request.getContent())) {
                throw new CustomException(ErrorCode.INVALID_MENTION);
            }

            mentionRepository.deleteAllInBatch(deleteMentions);
            mentionRepository.saveAll(newMentions);
        }
    }

    private boolean isInValidMention(final List<AppUser> users, String content) {
        return !users.stream().allMatch(user -> content.trim().contains("@" + user.getNickname()));
    }
}
