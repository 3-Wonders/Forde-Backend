
package com.project.forde.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "board_image")
@DynamicInsert
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long imageId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Board board;

    @Column(name = "image_size", nullable = false, columnDefinition = "INT UNSIGNED")
    private Long imageSize;

    @Column(name = "image_type", nullable = false, length = 20)
    private String imageType;

    @Column(name = "image_path", nullable = false, length = 200)
    private String imagePath;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간
}
