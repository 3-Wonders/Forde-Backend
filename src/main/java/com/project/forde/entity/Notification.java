
package com.project.forde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long notificationId;

    /**
     * 1001: 공지사항
     * 1002: 댓글
     * 1003: 좋아요
     * 1004: 추천 뉴스 | 게시글
     * 1005: 팔로우 게시글 (뉴스 | 게시글)
     * 1006: 이벤트
     */
    @Column(name = "notification_type", nullable = false, columnDefinition = "CHAR", length = 4)
    private String notificationType;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", columnDefinition = "INT UNSIGNED")
    private AppUser sender;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser receiver;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", columnDefinition = "INT UNSIGNED")
    private Board board;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", columnDefinition = "INT UNSIGNED")
    private Comment comment;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간
}
