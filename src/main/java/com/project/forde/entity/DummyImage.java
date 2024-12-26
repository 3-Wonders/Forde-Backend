
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
@Table(name = "dummy_image")
@DynamicInsert
public class DummyImage { // 진짜 게시글을 올릴 시 boardImage에 bummyImgae 데이터 올리길
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long imageId;

    @Column(name = "image_size", nullable = false, columnDefinition = "INT UNSIGNED")
    private Long imageSize;

    @Column(name = "image_type", nullable = false, length = 20)
    private String imageType;

    @Column(name = "image_path", nullable = false, length = 200)
    private String imagePath;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간

    @Builder
    public DummyImage(Long imageSize, String imageType, String imagePath) {
        this.imageSize = imageSize;
        this.imageType = imageType;
        this.imagePath = imagePath;
    }
}
