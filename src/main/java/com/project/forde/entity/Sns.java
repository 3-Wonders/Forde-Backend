package com.project.forde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sns")
@DynamicUpdate
public class Sns {
    @Id
    @Column(name = "sns_id", unique = true, nullable = false)
    private String snsId; // snsID

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED")
    private AppUser appUser;

    /**
     * 1001 : 카카오톡
     * 1002 : 네이버
     * 1003 : 구글
     * 1004 : 깃헙
     */
    @Column(name = "sns_kind", nullable = false, columnDefinition = "CHAR", length = 4)
    private String snsKind;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdTime; //생성된 시간
}
