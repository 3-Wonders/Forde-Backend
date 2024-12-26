package com.project.forde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false, columnDefinition = "INT UNSIGNED AUTO_INCREMENT")
    private Long userId; // 사용자ID

    @Column(name = "user_pw", nullable = false, length = 300)
    private String userPw; // 사용자PW => 암호화 => SNS 로그인 시 필요x

    @Column(name = "email", nullable = false, unique = true, length = 60)
    private String email; // 이메일

    @Column(name = "profile_path", length = 200)
    private String profilePath; // 프로필 사진

    @Column(name = "profile_size", length = 20, columnDefinition = "INT UNSIGNED")
    private Long profileSize; // 프로필 크기

    @Column(name = "profile_type", length = 20)
    private String profileType; // 프로필 사진 유형 ex) png, jpeg, xml, svg 등

    @Column(name = "nickname", length = 10, nullable = false, unique = true)
    private String nickname; // 닉네임

    @Column(name = "description", length = 40)
    private String description; // 자기소개

    @Column(name = "board_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Long boardCount; // 작성한 게시글 갯수

    @Column(name = "news_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Long newsCount; // 작성한 뉴스 갯수

    @Column(name = "comment_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Long commentCount; // 작성한 댓글 갯수

    @Column(name = "like_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Long likeCount; // 좋아요 누른 갯수

    @Column(name = "follower_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Long followerCount; // 팔로워 수(상대가)

    @Column(name = "following_count", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Long followingCount; // 팔로잉 수(내가)

    @Column(name = "disable_follow", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean disableFollow; // 팔로우 차단 설정 상태

    @Column(name = "private_account", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean privateAccount; // 비공개 설정 상태

    @Column(name = "notice_notification", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean noticeNotification; //공지사항 설정 상태

    @Column(name = "comment_notification", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean commentNotification; //댓글 설정 상태

    @Column(name = "like_notification", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean likeNotification; //좋아요 설정 상태

    @Column(name = "recommend_notification", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean recommendNotification; //추천 뉴스/게시글 설정 상태

    @Column(name = "follow_notification", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean followNotification; //팔로우 설정 상태

    @Column(name = "event_notification", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean eventNotification; //이벤트 설정 상태

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted; //계정 삭제 상태

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime; //삭제된 시간

    @CreationTimestamp
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime; //생성된 시간
}
