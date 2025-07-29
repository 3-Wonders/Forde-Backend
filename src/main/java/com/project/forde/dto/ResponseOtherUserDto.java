package com.project.forde.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseOtherUserDto {
    private Long userId;
    private String nickname;
    private String description;
    private String profilePath;
    private Integer followerCount;
    private Integer followingCount;
    private Integer boardCount;
    private Integer newsCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isPrivate;
    private Boolean isFollow;
}
