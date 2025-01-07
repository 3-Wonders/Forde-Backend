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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

            if (!isValidMention(users, request.getContent())) {
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

    private boolean isValidMention(final List<AppUser> users, String content) {
        return users.stream().allMatch(user -> content.trim().contains("@" + user.getNickname()));
    }
}
