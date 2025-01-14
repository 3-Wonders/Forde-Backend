package com.project.forde.dto.notification;

import com.project.forde.dto.appuser.AppUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class NotificationDto {
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
    }
}
