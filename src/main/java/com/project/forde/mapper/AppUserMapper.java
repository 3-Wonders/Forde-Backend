package com.project.forde.mapper;

import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.dto.sns.SnsDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.AppUser;
import com.project.forde.util.FileStore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = { FileUrlResolver.class })
public interface AppUserMapper {
    @Mapping(source = "request.email", target = "email")
    @Mapping(source = "request.password", target = "userPw")
    @Mapping(source = "request.isEnableNotification", target = "noticeNotification")
    @Mapping(source = "request.isEnableEvent", target = "eventNotification")
    AppUser toEntity(AppUserDto.Request.Signup request);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.description", target = "description")
    @Mapping(source = "user.profilePath", target = "profilePath", qualifiedByName = "getProfilePath")
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
    @Mapping(source = "user.profilePath", target = "profilePath", qualifiedByName = "getProfilePath")
    AppUserDto.Response.Intro toResponseIntroUserDto(AppUser user);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.description", target = "description")
    @Mapping(source = "user.profilePath", target = "profilePath", qualifiedByName = "getProfilePath")
    @Mapping(source = "interestedTags", target = "interestedTags")
    @Mapping(source = "user.boardCount", target = "boardCount")
    @Mapping(source = "user.newsCount", target = "newsCount")
    @Mapping(source = "user.likeCount", target = "likeCount")
    @Mapping(source = "user.commentCount", target = "commentCount")
    AppUserDto.Response.MyInfo toResponseMyInfoDto(AppUser user, List<TagDto.Response.TagWithoutCount> interestedTags);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "snsInfos", target = "snsInfos")
    AppUserDto.Response.Account toResponseAccountDto(AppUser user, List<SnsDto.Response.connectedStatus> snsInfos);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.profilePath", target = "profilePath", qualifiedByName = "getProfilePath")
    AppUserDto.Response.SearchUserNickname toResponseSearchNicknameDto(AppUser user);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.noticeNotification", target = "noticeNotification")
    @Mapping(source = "user.commentNotification", target = "commentNotification")
    @Mapping(source = "user.likeNotification", target = "likeNotification")
    @Mapping(source = "user.recommendNotification", target = "recommendNotification")
    @Mapping(source = "user.followNotification", target = "followNotification")
    @Mapping(source = "user.eventNotification", target = "eventNotification")
    AppUserDto.Response.MyNotificationInfo toResponseMyNotificationInfoDto(AppUser user);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.privateAccount", target = "privateAccount")
    @Mapping(source = "user.disableFollow", target = "disableFollow")
    AppUserDto.Response.MySnsInfo toResponseSnsDto(AppUser user);
}
