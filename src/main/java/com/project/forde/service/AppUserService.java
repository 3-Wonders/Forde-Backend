package com.project.forde.service;

import com.project.forde.dto.RequestLoginDto;
import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.entity.AppUser;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.AppUserMapper;
import com.project.forde.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseOtherUserDto getOtherUser(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return AppUserMapper.INSTANCE.toResponseOtherUserDto(user);
    }

    public void createAppUser(AppUserDto.Request request) {
        AppUser appuser = appUserRepository.findByEmail(request.getEmail());

        if (appuser != null) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        if (appUserRepository.findByNickname(request.getNickname()) != null) {
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }

        AppUser newUser = null;
        newUser = AppUserMapper.INSTANCE.toEntity(request);

        newUser.setUserPw(passwordEncoder.encode(request.getPassword()));

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
        AppUser user = appUserRepository.findByEmail(dto.getEmail());

        System.out.println(user);
        if(user == null) {
            throw new CustomException(ErrorCode.NOT_MATCHED_LOGIN_INFO);
        }

        if(!passwordEncoder.matches(dto.getPassword(), user.getUserPw())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_LOGIN_INFO);
        }

        if(!user.getVerified()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_USER);
        }

        return user.getUserId();
    }

}
