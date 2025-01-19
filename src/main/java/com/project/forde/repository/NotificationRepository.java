package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByReceiverOrderByNotificationIdDesc(Pageable pageable, AppUser user);
}
