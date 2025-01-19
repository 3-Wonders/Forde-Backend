package com.project.forde.service;

import com.project.forde.dto.notification.NotificationDto;
import com.project.forde.entity.*;
import com.project.forde.entity.composite.NotificationReadPK;
import com.project.forde.mapper.NotificationMapper;
import com.project.forde.repository.NotificationReadRepository;
import com.project.forde.repository.NotificationRepository;
import com.project.forde.repository.SseRepository;
import com.project.forde.type.NotificationTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {
    private static final Long TIME_OUT = 30L * 60L * 1000L;

    private final SseRepository sseRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;

    private final AppUserService appUserService;

    @Transactional
    public SseEmitter subscribe(String lastEventId) {
        AppUser user = appUserService.verifyUserAndGet(1L);
        String emitterId = user.getUserId() + "_" + System.currentTimeMillis();
        SseEmitter emitter = sseRepository.save(emitterId, new SseEmitter(TIME_OUT));

        sendNotification(null, user, NotificationTypeEnum.CONNECTED, null, null);

        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, user.getUserId().toString(), emitterId, emitter);
        }

        return emitter;
    }

    public NotificationDto.Response.Notifications getNotifications(int page, int count) {
        // TODO: User ID를 27로 고정하고 있으니, 추후 수정이 필요합니다.
        AppUser user = appUserService.verifyUserAndGet(27L);
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);
        Page<Notification> pages = notificationRepository.findAllByReceiverOrderByNotificationIdDesc(pageable, user);

        List<Notification> notifications = pages.getContent();
        List<NotificationDto.Response.Notification> response = notifications.stream().map(
                notification -> NotificationMapper.INSTANCE.toNotificationDto(
                        notification,
                        getMessage(notification)
                )
        ).toList();
        Long total = pages.getTotalElements();

        return new NotificationDto.Response.Notifications(response, total);
    }

    public void sendEmitter(SseEmitter emitter, String eventId, String emitterId, Object data) {
        if (data instanceof Notification notification) {
            String message = getMessage(notification);
            data = NotificationMapper.INSTANCE.toNotificationDto(
                    notification,
                    message
            );
        }

        try {
            emitter.send(
                    SseEmitter.event()
                        .id(eventId)
                        .name("notification")
                        .data(data)
            );
        } catch (IOException e) {
            log.error("Error in sendEmitter", e);
            sseRepository.deleteById(emitterId);
        }
    }

    @Transactional
    public void readNotification(List<Long> notificationIds) {
        AppUser user = appUserService.verifyUserAndGet(27L);
        List<NotificationRead> alreadyReads = notificationReadRepository
                .findAllByNotificationReadPK_ReaderAndNotificationReadPK_NotificationNotificationIdIn(
                                user,
                                notificationIds
                        );

        List<Long> excludeIds = alreadyReads.stream()
                .map(notificationRead -> notificationRead.getNotificationReadPK().getNotification().getNotificationId())
                .toList();

        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        // 이미 읽은 알림은 제외하고, 읽지 않은 알림만 읽은 알림으로 저장합니다.
        List<NotificationRead> createReads = notifications.stream()
                .filter(notification -> !excludeIds.contains(notification.getNotificationId()))
                .map(notification -> NotificationRead.builder()
                        .notificationReadPK(NotificationReadPK.builder()
                                .reader(user)
                                .notification(notification)
                                .build())
                        .build()
                )
                .toList();

        if (createReads.isEmpty()) {
            log.warn("No new notification to read : " +
                    "user_id = {}, notification_ids = {}", user.getUserId(), notificationIds);
            return;
        }

        notificationReadRepository.saveAll(createReads);
    }

    private boolean hasLostData(String lastEventId) {
        return lastEventId != null && !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userId, String emitterId, SseEmitter emitter) {
        ConcurrentHashMap<String, Object> eventCache = sseRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
        eventCache.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> {
                    sendEmitter(emitter, entry.getKey(), emitterId, entry.getValue());
                    sseRepository.deleteEventCacheById(entry.getKey());
                });
    }

    private String getMessage(Notification notification) {
        if (notification.getNotificationType().equals(NotificationTypeEnum.CONNECTED.getType())) {
            return "Connected";
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.NOTICE.getType())) {
            return String.format("\"%s\" 공지사항이 등록되었습니다.", notification.getBoard().getTitle());
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.BOARD_COMMENT.getType())) {
            return String.format("\"%s\" 게시글에 댓글이 등록되었습니다.", notification.getBoard().getTitle());
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.MENTION.getType())) {
            return String.format("\"%s\"님이 회원님을 언급하였습니다.", notification.getSender().getNickname());
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.BOARD_LIKE.getType())) {
            return String.format("\"%s\" 게시글에 좋아요가 등록되었습니다.", notification.getBoard().getTitle());
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.RECOMMEND.getType())) {
            return String.format("\"%s\"라는 글을 한 번 읽어보세요 !", notification.getBoard().getTitle());
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.FOLLOWING_POST.getType())) {
            return String.format("\"%s\"님이 새로운 글을 작성하였습니다.", notification.getSender().getNickname());
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.EVENT.getType())) {
            return String.format("\"%s\" 이벤트가 등록되었습니다.", notification.getBoard().getTitle());
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.FOLLOW.getType())) {
            return String.format("\"%s\"님이 회원님을 팔로우하였습니다.", notification.getSender().getNickname());
        } else if (notification.getNotificationType().equals(NotificationTypeEnum.FOLLOWING.getType())) {
            return String.format("\"%s\"님이 팔로우하였습니다.", notification.getSender().getNickname());
        } else {
            return "Unknown";
        }
    }

    /**
     * 수신자에게 알림을 전송합니다.
     * <br /> <br />
     * 알림을 DB에 저장하고, 수신자의 emitter에 알림을 전송합니다.
     * 단, type이 CONNECTED인 경우에는 DB에 저장하지 않습니다.
     * <br /> <br />
     * ●주의● : 이 메서드는 비동기로 실행됩니다.
     *
     * @param sender 발신자
     * @param receiver 수신자
     * @param type 알림 유형
     * @param board 게시글 (댓글, 좋아요, 언급 등의 경우)
     * @param comment 댓글 (댓글, 좋아요, 언급 등의 경우)
     */
    @Async
    public void sendNotification(
            @Nullable AppUser sender,
            AppUser receiver,
            NotificationTypeEnum type,
            @Nullable Board board,
            @Nullable Comment comment) {
        if (sender != null && sender.getUserId().equals(receiver.getUserId())) {
            return;
        }

        Notification notification;

        if (!type.equals(NotificationTypeEnum.CONNECTED)) {
            notification = notificationRepository.save(createNotification(sender, receiver, type, board, comment));
        } else {
            notification = createNotification(null, receiver, type, null, null);
        }

        String receiverId = receiver.getUserId().toString();
        String eventId = receiverId + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> emitters = sseRepository.findAllEmitterStartWithByUserId(receiverId);
        emitters.forEach((emitterId, emitter) -> {
            sseRepository.saveEventCache(eventId, notification);
            sendEmitter(emitter, eventId, emitterId, notification);
        });
    }

    private Notification createNotification(
            @Nullable AppUser sender,
            AppUser receiver,
            NotificationTypeEnum type,
            @Nullable Board board,
            @Nullable Comment comment) {
        return Notification.builder()
                .notificationType(type.getType())
                .sender(sender)
                .receiver(receiver)
                .board(board)
                .comment(comment)
                .build();
    }
}
