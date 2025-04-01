package com.project.forde.service;

import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Sns;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
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

    /**
     * 소셜 로그인 성공 후 진입하는 서비스 입니다.
     * 1. 세션이 존재하지 않는다면 로그인 또는 회원가입으로 판단
     * 2. 세션이 존재한다면 SNS 연동으로 판단
     * 판단을 마친 후 1 or 2에 알맞은 서비스 내의 메소드를 호출 합니다.
     * @param oAuth2User provider 에게서 받은 정보 입니다.
     * @param socialType SNS 타입(카카오, 구글, 네이버, 깃헙 등)
     * @param request 세션을 얻기 위한 request
     * @return userId(유저 아이디)
     */
    public Long socialAuth(OAuth2User oAuth2User, String socialType, HttpServletRequest request) {
        SocialTypeEnum socialTypeEnum = SocialTypeEnum.valueOf(socialType.toUpperCase());
        String snsKind = socialTypeEnum.getSnsKind();
        Long userId = (Long) request.getSession().getAttribute("userId");
        log.info("userId : {}", userId);
        if(userId != null){
            log.info("계정 연동 진입");
            linkAccountSns(oAuth2User, snsKind, userId);
            return userId;
        }

        return snsAuth(oAuth2User, snsKind);
    }

    /**
     * 각 provider 로부터 얻은 snsId를 통해 유저를 검색 한 후 유저가 존재하면 로그인, 존재하지 않으면 회원가입을 수행합니다.
     * @param oAuth2User Provider 에게서 받은 정보 입니다.
     * @param snsKind SNS 타입에 따른 고유 번호
     * @return userId(유저 아이디)
     */
    public Long snsAuth(OAuth2User oAuth2User, String snsKind) {
        String socialId = null;
        String profilePath = null;
        String email = null;
        switch (snsKind) {
            case "1001" : // 카카오
                String kakaoSnsId = oAuth2User.getAttribute("id").toString();
                assert kakaoSnsId != null;
                socialId = kakaoSnsId.toString();
                Map<String, Object> profile;

                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                if(kakaoAccount == null) {
                    throw new CustomException(ErrorCode.NOT_FOUND_SNS_ACCOUNT);
                }

                profile = (Map<String, Object>) kakaoAccount.get("profile");

                if(profile.get("profile_image_url") != null) {
                    profilePath = (String) profile.get("profile_image_url");
                }

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
            case "1004" : // 깃허브
                Integer githubSnsId = oAuth2User.getAttribute("id");
                assert githubSnsId != null;
                socialId = githubSnsId.toString();
                email = oAuth2User.getAttribute("email");
                if(oAuth2User.getAttribute("avatar_url") != null) {
                    profilePath = oAuth2User.getAttribute("avatar_url");
                    log.info("깃허브 프로필 주소 : {}", profilePath);
                }
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
            return sns.get().getAppUser().getUserId();
        }
    }

    /**
     * 새로운 SNS 계정 및 AppUser 를 생성합니다.
     * @param socialId 각 Provider 로부터 받은 sns 고유 번호
     * @param email SNS 이메일
     * @param snsKind SNS 타입에 따른 고유 번호
     * @param profilePath 각 Provider 로부터 받은 프로필 사진 주소
     * @return
     */
    public Long create(String socialId, String email, String snsKind, String profilePath) {
        Optional<Sns> sns = snsRepository.findBySnsId(socialId);

        if(sns.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_SNS_ACCOUNT);
        }

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

    /**
     * SNS 아이디를 반환 합니다.
     * @param oAuth2User Provider 에게서 받은 정보 입니다.
     * @param snsKind SNS 타입에 따른 고유번호 입니다.
     * @return socialId (SNS 고유 아이디)
     */
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

    /**
     * SNS 연동을 수행합니다.
     * @param oAuth2User Provider 에게서 받은 정보 입니다.
     * @param snsKind SNS 타입에 따른 고유번호 입니다.
     * @param userId Session 을 통해 얻은 유저 아이디
     */
    public void linkAccountSns(OAuth2User oAuth2User, String snsKind, Long userId) {
        String snsId = getSnsId(oAuth2User, snsKind);
        Optional<Sns> snsUser = snsRepository.findBySnsId(snsId);

        if(snsUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_SNS_ACCOUNT);
        }
        
        AppUser appUser = appUserService.getUser(userId);

        Sns newSnsUser = new Sns();
        newSnsUser.setSnsId(snsId);
        newSnsUser.setSnsKind(snsKind);
        newSnsUser.setAppUser(appUser);
        snsRepository.save(newSnsUser);
    }

    /**
     * 이메일 설정 및 인증을 신청한 사용자의 SNS 계정 유효성 검사 수행 및 이메일 설정 서비스를 호출합니다.
     * @param snsId SNS 사용자의 고유번호
     * @param email 설정하고자 하는 이메일
     * @return userId(유저 아이디)
     */
    public Long setSnsUserEmail(String snsId, String email) {
        Sns sns = snsRepository.findBySnsId(snsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Long userId = sns.getAppUser().getUserId();

        appUserService.updateEmail(userId, email);

        return userId;
    }

}
