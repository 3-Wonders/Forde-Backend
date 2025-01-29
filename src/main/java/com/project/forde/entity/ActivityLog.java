package com.project.forde.entity;

import com.project.forde.converter.LogTypeConverter;
import com.project.forde.type.LogTypeEnum;
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
@Table(
        name = "activity_log",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_log_type", columnList = "log_type"),
                @Index(name = "idx_created_time", columnList = "created_time")
        }
)
@DynamicInsert
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long logId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser user;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", columnDefinition = "INT UNSIGNED")
    private Board board;

    @Column(name = "log_type", nullable = false)
    @Convert(converter = LogTypeConverter.class)
    private LogTypeEnum logType;

    @Column(name = "duration", columnDefinition = "INT UNSIGNED")
    private Long duration;

    @Column(name = "revisit_count", columnDefinition = "INT UNSIGNED")
    private Long revisitCount;

    @Column(name = "keyword", length = 255)
    private String keyword;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime;
}
