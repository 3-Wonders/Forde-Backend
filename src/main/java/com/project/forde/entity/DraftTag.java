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
@Table(name = "draft_tag")
@DynamicInsert
public class DraftTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "draft_tag_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long draftTagId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Draft draft;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Tag tag;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime;
}
