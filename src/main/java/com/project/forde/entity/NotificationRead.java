
package com.project.forde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notification_read")
public class NotificationRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "read_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long readId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Notification notification;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", columnDefinition = "INT UNSIGNED")
    private AppUser reader;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간
}
