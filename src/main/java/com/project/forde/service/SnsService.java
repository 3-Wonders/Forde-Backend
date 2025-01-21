package com.project.forde.service;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Sns;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.SnsRepository;
import com.project.forde.type.SocialTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final AppUserRepository appUserRepository;

    public Long socialAuth(OAuth2User oAuth2User, String socialType, HttpServletRequest request) {
        SocialTypeEnum socialTypeEnum = SocialTypeEnum.valueOf(socialType.toUpperCase());
        String snsKind = socialTypeEnum.getSnsKind();
        Long userId = (Long) request.getSession().getAttribute("userId");

        if(userId != null){
            linkAccountSns(oAuth2User, snsKind, userId);
            return userId;
        }
        return snsAuth(oAuth2User, snsKind);
    }

    public Long snsAuth(OAuth2User oAuth2User, String snsKind) {
        String socialId = null;
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
                profilePath = (String) naverResponse.get("profile_image");

                break;
            case "1003" : // 구글
                socialId = oAuth2User.getAttribute("sub");
                email = oAuth2User.getAttribute("email");
                profilePath = oAuth2User.getAttribute("picture");
                break;
            case "1004" :
                Integer githubSnsId = oAuth2User.getAttribute("id");
                assert githubSnsId != null;
                socialId = githubSnsId.toString();
                email = oAuth2User.getAttribute("email");
                profilePath = oAuth2User.getAttribute("avatar_url");
                break;
        }

        if(socialId == null || socialId.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SNS_ID);
        }

        Optional<Sns> sns = snsRepository.findBySnsId(socialId);

        // 회원가입인 경우
        if (sns.isEmpty()) {
            return create(socialId, email, snsKind, profilePath);
        }
        // 로그인일 경우
        else {
            Long userId = sns.get().getAppUser().getUserId();
            AppUser appUser = appUserRepository.findByUserId(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
            if(appUser.getDeleted()) {
                throw new CustomException(ErrorCode.DELETED_USER);
            }
            return userId;
        }
    }

    public Long create(String socialId, String email, String snsKind, String profilePath) {
        // AppUser 계정 생성
        AppUser newAppUser = appUserService.createSnsUser(email, profilePath);

        // SNS 계정 생성
        Sns newSnsUser = new Sns();
        newSnsUser.setSnsId(socialId);
        newSnsUser.setSnsKind(snsKind);
        newSnsUser.setAppUser(newAppUser);
        snsRepository.save(newSnsUser);

        return newAppUser.getUserId();
    }

    public String getSnsId(OAuth2User oAuth2User, String snsKind) {
        String socialId = null;

        switch (snsKind) {
            case "1001" : // 카카오
                Long kakaoSnsId = oAuth2User.getAttribute("id");
                assert kakaoSnsId != null;
                socialId = kakaoSnsId.toString();
                break;
            case "1002" : // 네이버
                Map<String, Object> attributes = oAuth2User.getAttributes();
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                if(naverResponse == null) {
                    throw new CustomException(ErrorCode.NOT_FOUND_SNS_ACCOUNT);
                }
                socialId = (String) naverResponse.get("id");
                break;
            case "1003" : // 구글
                socialId = oAuth2User.getAttribute("sub");
                break;
            case "1004" : // 깃헙
                Integer githubSnsId = oAuth2User.getAttribute("id");
                assert githubSnsId != null;
                socialId = githubSnsId.toString();
                break;
        }
        if(socialId == null || socialId.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SNS_ID);
        }

        return socialId;
    }

    public void linkAccountSns(OAuth2User oAuth2User, String snsKind, Long userId) {
        String snsId = getSnsId(oAuth2User, snsKind);
        Optional<Sns> snsUser = snsRepository.findBySnsId(snsId);

        if(snsUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_SNS_ACCOUNT);
        }
        
        AppUser appUser = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Sns newSnsUser = new Sns();
        newSnsUser.setSnsId(snsId);
        newSnsUser.setSnsKind(snsKind);
        newSnsUser.setAppUser(appUser);
        snsRepository.save(newSnsUser);
    }
}
