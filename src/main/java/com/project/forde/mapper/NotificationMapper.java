package com.project.forde.mapper;

import com.project.forde.dto.notification.NotificationDto;
import com.project.forde.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CustomTimestampMapper.class)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(source = "notification.notificationId", target = "notificationId")
    @Mapping(source = "notification.notificationType", target = "notificationType")
    @Mapping(source = "notification.sender", target = "sender")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "notification.board.boardId", target = "boardId")
    @Mapping(source = "notification.isRead", target = "isRead")
    @Mapping(source = "notification.createdTime", target = "createdTime", qualifiedBy = { MapCreatedTime.class, CustomTimestampTranslator.class }, defaultExpression = "java(null)")
    NotificationDto.Response.Notification toNotificationDto(Notification notification, String message);
}
