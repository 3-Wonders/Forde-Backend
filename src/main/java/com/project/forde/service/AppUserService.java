package com.project.forde.service;

import com.project.forde.dto.RequestLoginDto;
import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.dto.board.BoardDto;
import com.project.forde.dto.sns.SnsDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.*;
import com.project.forde.entity.composite.InterestTagPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.AppUserMapper;
import com.project.forde.mapper.BoardMapper;
import com.project.forde.mapper.InterestTagMapper;
import com.project.forde.mapper.TagMapper;
import com.project.forde.repository.*;
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

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final InterestTagRepository interestTagRepository;
    private final TagRepository tagRepository;
    private final SnsRepository snsRepository;
    private final BoardRepository boardRepository;
    private final BoardTagRepository boardTagRepository;
    private final BoardService boardService;
    private final GetCookie getCookie;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    public ResponseOtherUserDto getOtherUser(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return AppUserMapper.INSTANCE.toResponseOtherUserDto(user);
    }

    public AppUserDto.Response.Intro getIntroUser(HttpServletRequest request) {
        Long userId = getCookie.getUserId(request);

        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return AppUserMapper.INSTANCE.toResponseIntroUserDto(user);
    }

    public AppUserDto.Response.myInfo getMyInfo(HttpServletRequest request) {
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

    public BoardDto.Response.Boards getUserNews(Long userId, final int page, final int count) {
        AppUser appUser = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<Board> boards = boardRepository.findAllByUploaderOrderByCreatedTimeDesc(pageable, appUser);

        return boardService.createBoardsDto(boards);
    }

    public List<AppUserDto.Response.searchUserNickname> getSearchUserNickname(final int page, final int count, String nickname) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<AppUser> appUsers = appUserRepository.findAllByNicknameContaining(pageable, nickname);

        return appUsers.stream().map(AppUserMapper.INSTANCE::toResponseSearchNicknameDto).toList();
    }

    public void updateMyInfo(AppUserDto.Request.updateMyInfo dto, HttpServletRequest request) {
        Long userId = getCookie.getUserId(request);
        AppUser appUser = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        appUser.setNickname(dto.getNickname());
        appUser.setDescription(dto.getDescription());
        appUserRepository.save(appUser);

        List<Tag> tags = tagRepository.findAllByTagIdIn(dto.getInterestTags());

        List<InterestTag> userInterestTag = interestTagRepository.findAllById_AppUser(appUser);
        interestTagRepository.deleteAll(userInterestTag);
        List<InterestTag> newInterestTag = new ArrayList<>();

        for (Tag tag : tags) {
            InterestTagPK interestTagPK = InterestTagMapper.INSTANCE.toPK(tag, appUser);

            InterestTag interestTag = new InterestTag();
            interestTag.setId(interestTagPK);

            newInterestTag.add(interestTag);
        }

        interestTagRepository.saveAll(newInterestTag);
    }

    public void createAppUser(AppUserDto.Request.signup request) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(request.getEmail());

        if (appUser.isPresent()) { // 이메일 중복
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        AppUser newUser = AppUserMapper.INSTANCE.toEntity(request);
        newUser.setUserPw(PasswordUtils.encodePassword(request.getPassword()));

        String name;
        do {
            name = "User_" + RandomStringUtils.random(7, 48, 122, true, true);
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
            name = "User_" + RandomStringUtils.random(7, 48, 122, true, true);
        } while (appUserRepository.findByEmail(name).isPresent());

        newAppUser.setEmail(email);
        if(email != null) newAppUser.setVerified(true);
        newAppUser.setNickname(name);
        newAppUser.setProfilePath(profilePath);
        appUserRepository.save(newAppUser);
        return newAppUser;
    }

    public void updatePassword(AppUserDto.Request.updatePassword dto, HttpServletRequest request) {
        mailService.compareRandomKey(dto.getRandomKey(), request);

        Long userId = getCookie.getUserId(request);
        AppUser appUser = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        appUser.setUserPw(PasswordUtils.encodePassword(dto.getPassword()));

        appUserRepository.save(appUser);

        redisTemplate.delete("email:randomKey:" + userId);
    }

    public void updateSocialSetting(AppUserDto.Request.updateSocialSetting dto, HttpServletRequest request) {
        Long userId = getCookie.getUserId(request);

        AppUser appUser = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        appUser.setDisableFollow(dto.getDisableFollow());
        appUser.setPrivateAccount(dto.getDisableAccount());

        appUserRepository.save(appUser);
    }

    public void updateNotificationSetting(AppUserDto.Request.updateNotificationSetting dto, HttpServletRequest request) {
        Long userId = getCookie.getUserId(request);

        AppUser appUser = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        appUser.setNoticeNotification(dto.getNoticeNotification());
        appUser.setCommentNotification(dto.getCommentNotification());
        appUser.setLikeNotification(dto.getLikeNotification());
        appUser.setFollowNotification(dto.getFollowNotification());
        appUser.setRecommendNotification(dto.getRecommendNotification());
        appUser.setEventNotification(dto.getEventNotification());
        appUserRepository.save(appUser);
    }
}
