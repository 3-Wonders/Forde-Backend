package com.project.forde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comment")
@DynamicInsert
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long commentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", columnDefinition = "INT UNSIGNED")
    private Comment parent;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", columnDefinition = "INT UNSIGNED")
    private AppUser uploader;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Board board;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; //댓글 내용

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간

    @CreationTimestamp
    @Column(name = "updated_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedTime; //업데이트 시간
}
