package com.project.forde.service;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Sns;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.SnsRepository;
import com.project.forde.type.SocialTypeEnum;
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
        String snsKind;
        Long userId;
        try {
            SocialTypeEnum socialTypeEnum = SocialTypeEnum.valueOf(socialType.toUpperCase());
            snsKind = socialTypeEnum.getSnsKind();
            userId = snsAuth(oAuth2User, snsKind);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_SOCIAL_TYPE);
        }
        return userId;
    }

    public Long snsAuth(OAuth2User oAuth2User, String snsKind) {
        String socialId = null;
        String name = null;
        String profilePath = null;
        String email = null;
        switch (snsKind) {
            case "1001" : // 카카오
                Long kakaoSnsId = oAuth2User.getAttribute("id");
                assert kakaoSnsId != null;
                socialId = kakaoSnsId.toString();

                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                if(kakaoAccount == null) {
                    throw new CustomException(ErrorCode.NOT_FOUND_SNS_ACCOUNT);
                }

                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if(profile == null) {
                    throw new CustomException(ErrorCode.NOT_FOUND_SNS_PROFILE);
                }

                name = (String) profile.get("nickname");
                profilePath = (String) profile.get("profile_image_url");

                break;
            case "1002" : // 네이버
                Map<String, Object> attributes = oAuth2User.getAttributes();
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                if(naverResponse == null) {
                    throw new CustomException(ErrorCode.NOT_FOUND_SNS_ACCOUNT);
                }

                socialId = (String) naverResponse.get("id");
                email = (String) naverResponse.get("email");
                name = (String) naverResponse.get("nickname");
                profilePath = (String) naverResponse.get("profile_image");

                break;
            case "1003" : // 구글
                socialId = oAuth2User.getAttribute("sub");
                email = oAuth2User.getAttribute("email");
                profilePath = oAuth2User.getAttribute("picture");
                name = oAuth2User.getAttribute("name");
                break;
            case "1004" :
                Integer githubSnsId = oAuth2User.getAttribute("id");
                assert githubSnsId != null;
                socialId = githubSnsId.toString();
                email = oAuth2User.getAttribute("email");
                profilePath = oAuth2User.getAttribute("avatar_url");
                name = oAuth2User.getAttribute("name");
                break;
        }

        if(socialId == null || socialId.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SNS_ID);
        }

        if(name == null) {
            name = "anonymous";
        }

        Optional<Sns> sns = snsRepository.findBySnsId(socialId);

        // 회원가입인 경우
        if (sns.isEmpty()) {
            return create(socialId, email, snsKind, name, profilePath);
        }
        // 로그인일 경우
        else {
            return sns.get().getAppUser().getUserId();
        }
    }

    public Long create(String socialId, String email, String snsKind, String name, String profilePath) {
        // AppUser 계정 생성
        AppUser newAppUser = appUserService.createSnsUser(email, name, profilePath);

        // SNS 계정 생성
        Sns newSnsUser = new Sns();
        newSnsUser.setSnsId(socialId);
        newSnsUser.setSnsKind(snsKind);
        newSnsUser.setAppUser(newAppUser);
        snsRepository.save(newSnsUser);

        return newAppUser.getUserId();
    }

}
