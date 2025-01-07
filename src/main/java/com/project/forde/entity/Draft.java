package com.project.forde.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "draft")
@NoArgsConstructor
@DynamicInsert
public class Draft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "draft_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long draftId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser uploader;

    @Column(name = "thumbnail_path", length = 100)
    private String thumbnailPath;

    @Column(name = "thumbnail_type", length = 20)
    private String thumbnailType;

    @Column(name = "thumbnail_size", columnDefinition = "INT UNSIGNED")
    private Long thumbnailSize;

    @Column(name = "category", nullable = false, columnDefinition = "CHAR(1)")
    private Character category;

    @Column(name = "title", length = 20)
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime;

    @CreationTimestamp
    @Column(name = "updated_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedTime;

    @Builder
    public Draft(AppUser uploader, String thumbnailPath, String thumbnailType, Long thumbnailSize, Character category, String title, String content) {
        this.uploader = uploader;
        this.thumbnailPath = thumbnailPath;
        this.thumbnailType = thumbnailType;
        this.thumbnailSize = thumbnailSize;
        this.category = category;
        this.title = title;
        this.content = content;
    }
}
