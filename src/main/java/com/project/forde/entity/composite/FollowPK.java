package com.project.forde.entity.composite;

import com.project.forde.entity.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class FollowPK implements Serializable {
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
}
