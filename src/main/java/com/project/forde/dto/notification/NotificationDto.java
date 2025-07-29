package com.project.forde.dto.notification;

import com.project.forde.dto.appuser.AppUserDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class NotificationDto {
    public static class Request {
        @Getter
        public static class ReadNotification {
            @NotNull(message = "알림 ID를 입력해주세요.")
            private List<Long> notificationIds;
        }
    }

    public static class Response {
        @Getter
        @AllArgsConstructor
        public static class Notification {
            private Long notificationId;
            private String notificationType;
            private AppUserDto.Response.Intro sender;
            private String message;
            private Long boardId;
            private Boolean isRead;
            private String createdTime;
        }

        @Getter
        @AllArgsConstructor
        public static class Notifications {
            List<Notification> notifications;
            Long total;
        }

        @Getter
        @AllArgsConstructor
        public static class FollowingUnReadCount {
            private Long unReadCount;
        }

        @Getter
        @AllArgsConstructor
        public static class FollowNewPost {
            private Boolean hasNewPosts;
        }
    }
}
