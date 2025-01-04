package com.project.forde.service;

import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.entity.AppUser;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.AppUserMapper;
import com.project.forde.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {
    private final AppUserRepository appUserRepository;

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
}
