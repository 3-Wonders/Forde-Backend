package com.project.forde.mapper;

import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.dto.sns.SnsDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CustomTimestampTranslator.class)
public interface AppUserMapper {
    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(source = "request.email", target = "email")
    @Mapping(source = "request.password", target = "userPw")
    @Mapping(source = "request.isEnableNotification", target = "noticeNotification")
    @Mapping(source = "request.isEnableEvent", target = "eventNotification")
    AppUser toEntity(AppUserDto.Request.signup request);

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

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.profilePath", target = "profilePath")
    AppUserDto.Response.Intro toResponseIntroUserDto(AppUser user);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.description", target = "description")
    @Mapping(source = "user.profilePath", target = "profilePath")
    @Mapping(source = "interestedTags", target = "interestedTags")
    @Mapping(source = "user.boardCount", target = "boardCount")
    @Mapping(source = "user.newsCount", target = "newsCount")
    @Mapping(source = "user.likeCount", target = "likeCount")
    @Mapping(source = "user.commentCount", target = "commentCount")
    AppUserDto.Response.myInfo toResponseMyInfoDto(AppUser user, List<TagDto.Response.Tag> interestedTags);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "snsInfos", target = "snsInfos")
    AppUserDto.Response.account toResponseAccountDto(AppUser user, List<SnsDto.Response.connectedStatus> snsInfos);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.profilePath", target = "profilePath")
    AppUserDto.Response.searchUserNickname toResponseSearchNicknameDto(AppUser user);
}
