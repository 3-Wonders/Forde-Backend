package com.project.forde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "follow")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long followId;

    // 보낸사람 (팔로워)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser follower;

    // 받는 사람 (팔로잉)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser following;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private LocalDateTime createdTime; //생성된 시간
}
