package com.project.forde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "interest_tag")
public class InterestTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_tag_id", nullable = false, unique = true, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long interestTagId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Tag tag;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser appUser;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private LocalDateTime createdTime; //생성된 시간
}
