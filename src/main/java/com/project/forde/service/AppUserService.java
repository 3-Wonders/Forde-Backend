package com.project.forde.service;

import com.project.forde.dto.RequestLoginDto;
import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.dto.sns.SnsDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.InterestTag;
import com.project.forde.entity.Sns;
import com.project.forde.entity.Tag;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.AppUserMapper;
import com.project.forde.mapper.TagMapper;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.InterestTagRepository;
import com.project.forde.repository.SnsRepository;
import com.project.forde.repository.TagRepository;
import com.project.forde.type.SocialTypeEnum;
import com.project.forde.util.GetCookie;
import com.project.forde.util.PasswordUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final InterestTagRepository interestTagRepository;
    private final TagRepository tagRepository;
    private final SnsRepository snsRepository;

    public ResponseOtherUserDto getOtherUser(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return AppUserMapper.INSTANCE.toResponseOtherUserDto(user);
    }

    public AppUserDto.Response.Intro getIntroUser(HttpServletRequest request) {
        GetCookie getCookie = new GetCookie(redisTemplate);
        Long userId = getCookie.getUserId(request);

        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return AppUserMapper.INSTANCE.toResponseIntroUserDto(user);
    }

    public AppUserDto.Response.myInfo getMyInfo(HttpServletRequest request) {
        GetCookie getCookie = new GetCookie(redisTemplate);
        Long userId = getCookie.getUserId(request);

        AppUser appUser = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<InterestTag> interestTags = interestTagRepository.findAllById_AppUser(appUser);
        List<Long> interestTagIds = interestTags.stream()
                .map(interestTag -> interestTag.getId().getTag().getTagId())
                .collect(Collectors.toList());
        List<Tag> tags = tagRepository.findAllByTagIdIn(interestTagIds);
        List<TagDto.Response.Tag> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

        return AppUserMapper.INSTANCE.toResponseMyInfoDto(appUser, responseTags);
    }

    public AppUserDto.Response.account getAccount(HttpServletRequest request) {
        GetCookie getCookie = new GetCookie(redisTemplate);
        Long userId = getCookie.getUserId(request);

        AppUser appUser = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<Sns> sns = snsRepository.findAllByAppUser(appUser);

        Set<String> snsKindsWithTrue = sns.stream()
                .map(Sns::getSnsKind)
                .collect(Collectors.toSet());

        List<SnsDto.Response.connectedStatus> snsInfos = Arrays.asList("1001", "1002", "1003", "1004").stream()
                .map(snsKind -> new SnsDto.Response.connectedStatus(
                        Integer.valueOf(snsKind),
                        SocialTypeEnum.fromSnsKind(snsKind).name(),
                        snsKindsWithTrue.contains(snsKind)
                ))
                .toList();

        return AppUserMapper.INSTANCE.toResponseAccountDto(appUser, snsInfos);

    }

    public List<AppUserDto.Response.searchUserNickname> getSearchUserNickname(final int page, final int count, String nickname) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<AppUser> appUsers = appUserRepository.findAllByNicknameContaining(pageable, nickname);

        return appUsers.stream().map(AppUserMapper.INSTANCE::toResponseSearchNicknameDto).toList();
    }

    public void createAppUser(AppUserDto.Request request) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(request.getEmail());

        if (appUser.isPresent()) { // 이메일 중복
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        AppUser newUser = AppUserMapper.INSTANCE.toEntity(request);
        newUser.setUserPw(PasswordUtils.encodePassword(request.getPassword()));

        String name;
        do {
            name = RandomStringUtils.random(10, 48, 122, true, true);
        } while (appUserRepository.findByEmail(name).isPresent());
        newUser.setNickname(name);

        if(request.getIsEnableNotification()) {
            newUser.setRecommendNotification(true);
            newUser.setNoticeNotification(true);
            newUser.setCommentNotification(true);
            newUser.setLikeNotification(true);
            newUser.setFollowNotification(true);
        }

        if(request.getIsEnableEvent()) {
            newUser.setEventNotification(true);
        }

        appUserRepository.save(newUser);
    }

    public Long login(RequestLoginDto dto) {
        AppUser user = appUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if(!PasswordUtils.checkPassword(dto.getPassword(), user.getUserPw())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_LOGIN_INFO);
        }

        if(!user.getVerified()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_USER);
        }

        return user.getUserId();
    }

    public AppUser createSnsUser(String email,String profilePath) {
        AppUser newAppUser = new AppUser();
        String name;

        do {
            name = RandomStringUtils.random(10, 48, 122, true, true);
        } while (appUserRepository.findByEmail(name).isPresent());

        newAppUser.setEmail(email);
        if(email != null) newAppUser.setVerified(true);
        newAppUser.setNickname(name);
        newAppUser.setProfilePath(profilePath);
        appUserRepository.save(newAppUser);
        return newAppUser;
    }

    public Long getRedisUserId(HttpServletRequest request) {
        GetCookie getCookie = new GetCookie(redisTemplate);
        Long userId = getCookie.getUserId(request);
        System.out.println("userId : " +  userId);
        System.out.println("session : " + request.getSession());
        System.out.println("sessionId : " + request.getSession().getId());
        return userId;
    }

}
