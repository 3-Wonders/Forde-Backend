package com.project.forde.batch.csv.user.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(
        Long userId,
        String nickname,
        String description,
        Long boardCount,
        Long newsCount,
        Long commentCount,
        Long likeCount,
        Long followerCount,
        Long followingCount
) {
    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", description='" + description + '\'' +
                ", boardCount=" + boardCount +
                ", newsCount=" + newsCount +
                ", commentCount=" + commentCount +
                ", likeCount=" + likeCount +
                ", followerCount=" + followerCount +
                ", followingCount=" + followingCount +
                '}';
    }
}