package com.project.forde.service;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Sns;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.SnsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsService extends DefaultOAuth2UserService {
    private final SnsRepository snsRepository;
    private final AppUserRepository appUserRepository;

    public void auth(OAuth2User oAuth2User, String socialType) {
        String socialId = oAuth2User.getAttribute("sub");
        String snsKind = checkSocialType(socialType);
        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");
        String name = oAuth2User.getAttribute("name");

        if(snsKind.equals("1005")) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if(socialId == null || socialId.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SNS_ID);
        }

        Sns sns = snsRepository.findBySnsId(socialId);

        // 회원가입인 경우
        if (sns == null) {
            // AppUser 계정 생성
            AppUser newAppUser = new AppUser();
            newAppUser.setEmail(email);
            newAppUser.setNickname(name);
            newAppUser.setProfilePath(picture);
            newAppUser.setVerified(!snsKind.equals("1001"));
            appUserRepository.save(newAppUser);

            Sns newSnsUser = new Sns();
            newSnsUser.setSnsId(socialId);
            newSnsUser.setSnsKind(snsKind);
            newSnsUser.setAppUser(newAppUser);
            snsRepository.save(newSnsUser);
        }
    }

    public String checkSocialType(String socialType) {
        if(socialType.equalsIgnoreCase("google")) {
            return "1003";
        }
        else {
            return "1005";
        }
    }
}
