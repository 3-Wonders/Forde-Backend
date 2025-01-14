package com.project.forde.service;

import com.project.forde.dto.comment.CommentDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Comment;
import com.project.forde.entity.Mention;
import com.project.forde.entity.composite.MentionPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.MentionRepository;
import com.project.forde.type.NotificationTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentionService {
    private final MentionRepository mentionRepository;
    private final AppUserRepository appUserRepository;

    private final AppUserService appUserService;
    private final NotificationService notificationService;

    public List<Mention> getMentionIn(final List<Comment> comments) {
        return mentionRepository.findAllByMentionPK_CommentIn(comments);
    }

    @Transactional
    public void create(final Long userId, final Comment createdComment, final CommentDto.Request request) {
        List<Long> mentionIds = request.getUserIds();
        String content = request.getContent();

        if (mentionIds != null && !mentionIds.isEmpty()) {
            List<Long> withOutMeUserIds = mentionIds.stream()
                    .filter(uid -> !uid.equals(userId))
                    .toList();
            List<AppUser> users = appUserRepository.findAllByUserIdIn(withOutMeUserIds);

            if (isInValidMention(users, content)) {
                throw new CustomException(ErrorCode.INVALID_MENTION);
            }

            List<Mention> mentions = users.stream().map(mentionUser -> {
                MentionPK mentionPK = new MentionPK(createdComment, mentionUser);
                Mention mention = new Mention();
                mention.setMentionPK(mentionPK);

                return mention;
            }).toList();

            if (mentions.isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_MENTION);
            }

            mentionRepository.saveAll(mentions);

            // TODO: Notification 발생
            mentions.forEach(mention -> {
                notificationService.sendNotification(
                        appUserService.getUser(userId),
                        mention.getMentionPK().getUser(),
                        NotificationTypeEnum.MENTION,
                        createdComment.getBoard(),
                        createdComment
                );
            });
        }
    }

    public boolean isInValidMention(final List<AppUser> users, String content) {
        return !users.stream().allMatch(user -> content.trim().contains("@" + user.getNickname()));
    }
}
