package com.project.forde.batch.csv.user.info;

import com.project.forde.batch.csv.user.dto.UserDto;
import com.project.forde.entity.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserCsvProcessor implements ItemProcessor<AppUser, UserDto> {
    @Override
    public UserDto process(AppUser item) {
        UserDto userDto = UserDto.builder()
                .userId(item.getUserId())
                .nickname(item.getNickname())
                .description(item.getDescription())
                .boardCount(item.getBoardCount())
                .newsCount(item.getNewsCount())
                .commentCount(item.getCommentCount())
                .likeCount(item.getLikeCount())
                .followerCount(item.getFollowerCount())
                .followingCount(item.getFollowingCount())
                .build();

        log.info("userDto: {}", userDto);
        return userDto;
    }
}
