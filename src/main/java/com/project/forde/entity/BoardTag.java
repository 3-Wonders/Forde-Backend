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
@Table(name = "board_tag")
@DynamicInsert
public class BoardTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_tag_id", nullable = false, unique = true, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long boardTagId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Board board; //=> ON UPDATE CASCADE 이 옵션은 mysql에서 직접 설정하기

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Tag tag; //=> ON UPDATE CASCADE 이 옵션은 mysql에서 직접 설정하기

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간
}
