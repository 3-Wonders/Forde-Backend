package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.NotificationRead;
import com.project.forde.entity.composite.NotificationReadPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, NotificationReadPK> {
    List<NotificationRead> findAllByNotificationReadPK_ReaderAndNotificationReadPK_NotificationNotificationIdIn
            (AppUser reader, List<Long> notificationIds);
}
