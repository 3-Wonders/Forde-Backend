package com.project.forde.service;

import com.project.forde.entity.Sns;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.SnsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsService extends DefaultOAuth2UserService {
    private final SnsRepository snsRepository;

    public void create(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String socialType = userRequest.getClientRegistration().getRegistrationId();
        String socialId = oAuth2User.getAttribute("sub");
        System.out.println(socialType);
        System.out.println(socialId);
        System.out.println(oAuth2User);

//        Sns sns = snsRepository.findBySnsId(Long.valueOf(socialId));
    }
}
