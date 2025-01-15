package com.project.forde.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "board")
@DynamicInsert
@DynamicUpdate
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long boardId;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser uploader;

    @Column(name = "category", nullable = false, columnDefinition = "CHAR", length = 1)
    private Character category;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // HTML이 저장됨

    @Column(name = "thumbnail_path", length = 100)
    private String thumbnailPath;

    @Column(name = "thumbnail_type", length = 20)
    private String thumbnailType;

    @Column(name = "thumbnail_size", columnDefinition = "INT UNSIGNED")
    private Long thumbnailSize;

    @Column(name = "like_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer likeCount;

    @Column(name = "comment_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer commentCount;

    @Column(name = "view_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer viewCount;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Formula(value = "(SELECT " +
                            "CASE WHEN EXISTS " +
                                "(" +
                                    "SELECT 1 FROM board_like bl " +
                                        "WHERE bl.board_id = board_id AND bl.user_id = uploader_id" +
                                ") " +
                            "THEN true ELSE false " +
                            "END)")
    private Boolean isLike;
}
