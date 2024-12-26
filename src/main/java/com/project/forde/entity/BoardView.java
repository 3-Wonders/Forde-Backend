
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
@Table(name = "board_view")
@DynamicInsert
public class BoardView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long viewId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser user; //=> ON UPDATE CASCADE 이 옵션은 mysql에서 직접 설정하기

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Board board; //=> ON UPDATE CASCADE 이 옵션은 mysql에서 직접 설정하기

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간
}
