package com.project.forde.service;

import com.project.forde.annotation.ExtractUserId;
import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.ExtractUserIdAspect;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.mail.MailDto;
import com.project.forde.entity.AppUser;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.util.RedisStore;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final AppUserRepository appUserRepository;
    private final RedisStore redisStore;
    private final AppUserService appUserService;

    public void sendEmail(String receiver, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
    }


    public void sendVerificationCode(MailDto.Request.Send dto) {
        appUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String code = RandomStringUtils.random(6, 33, 125, true, true);

        sendEmail(dto.getEmail(), "Forde 인증 코드", "Forde 인증 코드 입니다. : " + code);

        redisStore.set("email:verification:" + dto.getEmail(), "verificationCode", code, 10);
    }

    public Long verifyEmail(MailDto.Request.EmailVerification dto) {
        String storedCode = (String) redisStore.get("email:verification:" + dto.getEmail(), "verificationCode");

        if(storedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFIED_EMAIL);
        }

        if(!storedCode.equals(dto.getVerifyCode())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_VERIFIED_CODE);
        }

        Long userId = appUserService.setUserVerify(dto.getEmail());

        redisStore.deleteField("email:verification:" + dto.getEmail(), "verificationCode");

        return userId;
    }

    @UserVerify
    public void verifyEmailPassword(MailDto.Request.EmailVerification dto) {
        Long userId = UserVerifyAspect.getUserId();
        appUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String storedCode = (String) redisStore.get("email:verification:" + dto.getEmail(), "verificationCode");

        if(storedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFIED_EMAIL);
        }

        if(!storedCode.equals(dto.getVerifyCode())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_VERIFIED_CODE);
        }
        redisStore.deleteField("email:verification:" + dto.getEmail(), "verificationCode");


        String randomKey = RandomStringUtils.random(48, 33, 125, true, true);
        redisStore.set("email:randomKey:" + userId, "randomKeyValue", randomKey, 10);

        String title = "Forde 비밀번호 변경 페이지 URL";
        String content = "Forde 비밀번호 변경 페이지 URL 입니다. : http://localhost:5173/ch-password?key=" + randomKey;
        sendEmail(dto.getEmail(), title, content);
    }

    public void verifyRandomKey(Long userId, String randomKey) {
        String storedRandomKey = (String) redisStore.get("email:randomKey:" + userId, "randomKeyValue");

        if(storedRandomKey == null) {
            throw new CustomException(ErrorCode.EXPIRED_RANDOM_KEY);
        }
        if(!storedRandomKey.equals(randomKey)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_RANDOM_KEY);
        }
    }

    @UserVerify
    public void verifyRandomKeyWithUpdatePassword(MailDto.Request.UpdatePassword dto) {
        Long userId = UserVerifyAspect.getUserId();
        verifyRandomKey(userId, dto.getRandomKey());

        appUserService.updatePassword(dto.getPassword());
    }
}
