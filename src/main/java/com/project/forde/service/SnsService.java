package com.project.forde.service;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Sns;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.SnsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsService extends DefaultOAuth2UserService {
    private final SnsRepository snsRepository;
    private final AppUserService appUserService;

    public Long socialAuth(OAuth2User oAuth2User, String socialType) {
        String snsKind = "";

        if(socialType.equalsIgnoreCase("google")) {
            snsKind = "1003";
            return googleAuth(oAuth2User, snsKind);
        }
        else if(socialType.equalsIgnoreCase("kakao")) {
            snsKind = "1001";
            return kakaoAuth(oAuth2User, snsKind);
        }
        else {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public Long kakaoAuth(OAuth2User oAuth2User, String snsKind) {
        Long snsId = oAuth2User.getAttribute("id");
        assert snsId != null;
        String socialId = snsId.toString();

        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        if(kakaoAccount == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_SNS_ACCOUNT);
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if(profile == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_SNS_PROFILE);
        }

        String name = (String) profile.get("nickname");
        if(name == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_SNS_NAME);
        }

        String profilePath = (String) profile.get("profile_image_url");

        Optional<Sns> sns = snsRepository.findBySnsId(socialId);

        // 회원가입인 경우
        if (sns.isEmpty()) {
            create(socialId, null, snsKind, name, profilePath);
            return null;
        }
        // 로그인일 경우
        else {
            return sns.get().getAppUser().getUserId();
        }

    }

    public Long googleAuth(OAuth2User oAuth2User, String snsKind) {
        String socialId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String profilePath = oAuth2User.getAttribute("picture");
        String name = oAuth2User.getAttribute("name");

        if(socialId == null || socialId.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SNS_ID);
        }

        Optional<Sns> sns = snsRepository.findBySnsId(socialId);

        // 회원가입인 경우
        if (sns.isEmpty()) {
            create(socialId, email, snsKind, name, profilePath);
            return null;
        }
        // 로그인일 경우
        else {
            return sns.get().getAppUser().getUserId();
        }
    }

    public void create(String socialId, String email, String snsKind, String name, String profilePath) {
        // AppUser 계정 생성
        AppUser newAppUser = appUserService.createSnsUser(email, name, profilePath);

        // SNS 계정 생성
        Sns newSnsUser = new Sns();
        newSnsUser.setSnsId(socialId);
        newSnsUser.setSnsKind(snsKind);
        newSnsUser.setAppUser(newAppUser);
        snsRepository.save(newSnsUser);
    }

}
