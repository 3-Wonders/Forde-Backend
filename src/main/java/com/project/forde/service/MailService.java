package com.project.forde.service;

import com.project.forde.dto.RequestMailCompareDto;
import com.project.forde.dto.RequestMailDto;
import com.project.forde.entity.AppUser;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;
    private final AppUserRepository appUserRepository;

    public void sendEmail(RequestMailDto request) {
        AppUser appUser = appUserRepository.findByEmail(request.getEmail());

        if (appUser == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_VERIFIED_EMAIL);
        }

        String code = RandomStringUtils.random(6, 33, 125, true, true);

        redisTemplate.opsForValue().set("email:verification:" + request.getEmail(), code, 10, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Forde 회원가입 인증 코드");
        message.setText("Forde 회원가입 인증 코드 입니다. : " + code);
        mailSender.send(message);
    }

    public long compareEmail(RequestMailCompareDto request) {
        AppUser appUser = appUserRepository.findByEmail(request.getEmail());

        if (appUser == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }

        String storedCode = redisTemplate.opsForValue().get("email:verification:" + request.getEmail());

        if(storedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFIED_EMAIL);
        }

        if(!storedCode.equals(request.getVerifyCode())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_VERIFIED_CODE);
        }

        redisTemplate.delete("email:verification:" + request.getEmail());

        appUser.setVerified(true);
        appUserRepository.save(appUser);

        return appUser.getUserId();
    }
}
