package com.project.forde.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tag")
@DynamicInsert
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false, unique = true, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long tagId;

    @Column(name = "tag_name", nullable = false, unique = true, length = 10)
    private String tagName;

    @Column(name = "tag_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Long tagCount;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createTime;

    @Builder
    public Tag(String tagName) {
        this.tagName = tagName;
    }
}
