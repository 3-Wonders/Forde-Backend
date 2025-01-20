package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.NotificationRead;
import com.project.forde.entity.composite.NotificationReadPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.List;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, NotificationReadPK> {
    List<NotificationRead> findAllByNotificationReadPK_ReaderAndNotificationReadPK_NotificationNotificationIdIn
            (AppUser reader, List<Long> notificationIds);

    @Query(
            value = "SELECT COUNT(n.notificationId) " +
                    "FROM Notification n " +
                    "WHERE n.notificationType IN (:types) " +
                        "AND NOT EXISTS (" +
                            "SELECT 1 " +
                            "FROM NotificationRead nr " +
                            "WHERE nr.notificationReadPK.notification.notificationId = n.notificationId" +
                        ") " +
                        "AND EXISTS (" +
                            "SELECT 1 FROM Board b " +
                            "WHERE (:category IS NULL OR b.category = :category) " +
                                "AND b.boardId = n.board.boardId " +
                                "AND b.uploader.userId IN (" +
                                    "SELECT f.followPK.following.userId " +
                                    "FROM Follow f " +
                                    "WHERE f.followPK.follower.userId = :userId" +
                                ")" +
                        ")"
    )
    Long countByUnReadNotification(Long userId, String[] types, @Nullable Character category);
}
