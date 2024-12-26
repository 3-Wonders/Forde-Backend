package com.project.forde.mapper;

import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CustomTimestampTranslator.class)
public interface AppUserMapper {
    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.description", target = "description")
    @Mapping(source = "user.profilePath", target = "profilePath")
    @Mapping(source = "user.boardCount", target = "boardCount")
    @Mapping(source = "user.newsCount", target = "newsCount")
    @Mapping(source = "user.likeCount", target = "likeCount")
    @Mapping(source = "user.commentCount", target = "commentCount")
    @Mapping(source = "user.followerCount", target = "followerCount")
    @Mapping(source = "user.followingCount", target = "followingCount")
    @Mapping(source = "user.privateAccount", target = "isPrivate")
    ResponseOtherUserDto toResponseOtherUserDto(AppUser user);
}
