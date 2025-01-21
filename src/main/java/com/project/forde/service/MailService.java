package com.project.forde.service;

import com.project.forde.annotation.ExtractUserId;
import com.project.forde.aspect.ExtractUserIdAspect;
import com.project.forde.dto.mail.MailDto;
import com.project.forde.entity.AppUser;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;
    private final AppUserRepository appUserRepository;

    public void sendEmail(MailDto.Request.send request) {
        appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String code = RandomStringUtils.random(6, 33, 125, true, true);

        redisTemplate.opsForValue().set("email:verification:" + request.getEmail(), code, 10, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Forde 회원가입 인증 코드");
        message.setText("Forde 회원가입 인증 코드 입니다. : " + code);
        mailSender.send(message);
    }

    public long compareEmail(MailDto.Request.compareVerifyCode request) {
        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String storedCode = redisTemplate.opsForValue().get("email:verification:" + request.getEmail());

        if(storedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFIED_EMAIL);
        }

        if(!storedCode.equals(request.getVerifyCode())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_VERIFIED_CODE);
        }

        redisTemplate.delete("email:verification:" + request.getEmail());

        user.setVerified(true);
        appUserRepository.save(user);

        return user.getUserId();
    }

    @ExtractUserId
    public void compareEmailPassword(MailDto.Request.compareVerifyCode dto) {
        Long userId = ExtractUserIdAspect.getUserId();
        appUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String storedCode = redisTemplate.opsForValue().get("email:verification:" + dto.getEmail());

        if(storedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFIED_EMAIL);
        }

        if(!storedCode.equals(dto.getVerifyCode())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_VERIFIED_CODE);
        }

        redisTemplate.delete("email:verification:" + dto.getEmail());

        String randomKey = RandomStringUtils.random(48, 33, 125, true, true);
        redisTemplate.opsForValue().set("email:randomKey:" + userId, randomKey, 10, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getEmail());
        message.setSubject("Forde 비밀번호 변경 페이지 URL");
        message.setText("Forde 비밀번호 변경 페이지 URL 입니다. : http://localhost:5173/ch-password?key=" + randomKey);
        mailSender.send(message);
    }

    @ExtractUserId
    public void compareRandomKey(String randomKey) {
        Long userId = ExtractUserIdAspect.getUserId();
        String storedRandomKey = redisTemplate.opsForValue().get("email:randomKey:" + userId);

        if(storedRandomKey == null) {
            throw new CustomException(ErrorCode.EXPIRED_RANDOM_KEY);
        }
        if(!storedRandomKey.equals(randomKey)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_RANDOM_KEY);
        }
    }
}
