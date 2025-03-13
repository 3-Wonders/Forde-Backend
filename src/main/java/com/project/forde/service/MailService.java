package com.project.forde.service;

import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.mail.MailDto;
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
    private final SnsService snsService;

    /**
     * 이메일을 전송합니다.
     * @param receiver 수신자
     * @param title 제목
     * @param content 내용
     */
    public void sendEmail(String receiver, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
    }

    /**
     * 이메일 인증 유효성 검사를 수행합니다.
     * @param storedCode 레디스에 저장된 코드
     * @param verifyCode 사용자가 작성한 코드
     */
    public void checkKeyStatus(String storedCode, String verifyCode) {
        if(storedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFIED_EMAIL);
        }

        if(!storedCode.equals(verifyCode)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_VERIFIED_CODE);
        }
    }

    /**
     * 이메일 인증코드를 발송합니다.
     * @param dto (이메일)
     */
    public void sendVerificationCode(MailDto.Request.Send dto) {
        String code = RandomStringUtils.random(6, 33, 125, true, true);

        sendEmail(dto.getEmail(), "Forde 인증 코드", "Forde 인증 코드 입니다. : " + code);

        redisStore.set("email:verification:" + dto.getEmail(), "verificationCode", code, 10);
    }


    /**
     * 이메일 인증코드 유효성 검사 및 사용자 이메일 인증을 수행합니다.
     * @param dto (이메일, 발급받은 인증코드)
     * @return
     */
    public Long verifyEmail(MailDto.Request.EmailVerification dto) {
        String storedCode = (String) redisStore.get("email:verification:" + dto.getEmail(), "verificationCode");

        checkKeyStatus(storedCode, dto.getVerifyCode());

        Long userId = appUserService.setUserVerify(dto.getEmail());

        redisStore.deleteField("email:verification:" + dto.getEmail(), "verificationCode");

        return userId;
    }

    /**
     * 이메일 인증코드 유효성 검사 및 비밀번호 변경을 수행합니다.
     * @param dto (이메일, 발급받은 인증코드)
     */
    @UserVerify
    public void verifyEmailPassword(MailDto.Request.EmailVerification dto) {
        Long userId = UserVerifyAspect.getUserId();
        appUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String storedCode = (String) redisStore.get("email:verification:" + dto.getEmail(), "verificationCode");

        checkKeyStatus(storedCode, dto.getVerifyCode());

        redisStore.deleteField("email:verification:" + dto.getEmail(), "verificationCode");


        String randomKey = RandomStringUtils.random(48, 33, 125, true, true);
        redisStore.set("email:randomKey:" + userId, "randomKeyValue", randomKey, 10);

        String title = "Forde 비밀번호 변경 페이지 URL";
        String content = "Forde 비밀번호 변경 페이지 URL 입니다. : http://localhost:5173/ch-password?key=" + randomKey;
        sendEmail(dto.getEmail(), title, content);
    }

    /**
     * 랜덤키 유효성 검사를 수행합니다.
     * @param userId 사용자 아이디
     * @param randomKey 사용자가 발급받은 랜덤키
     */
    public void verifyRandomKey(Long userId, String randomKey) {
        String storedRandomKey = (String) redisStore.get("email:randomKey:" + userId, "randomKeyValue");

        if(storedRandomKey == null) {
            throw new CustomException(ErrorCode.EXPIRED_RANDOM_KEY);
        }
        if(!storedRandomKey.equals(randomKey)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_RANDOM_KEY);
        }
    }

    /**
     * 랜덤키 유효성 검사 및 사용자 비밀번호 변경를 수행합니다.
     * @param dto (변경할 비밀번호, 발급받은 랜덤키)
     */
    @UserVerify
    public void verifyRandomKeyWithUpdatePassword(MailDto.Request.UpdatePassword dto) {
        Long userId = UserVerifyAspect.getUserId();
        verifyRandomKey(userId, dto.getRandomKey());

        appUserService.updatePassword(dto.getPassword());
    }

    /**
     * 사용자의 이메일 변경 전, 인증코드 유효성 검사를 하고 유저의 이메일을 변경하는 서비스를 호출합니다.
     * @param dto (이메일, 인증코드)
     */
    public void verifyEmailCodeWithUpdateEmail(MailDto.Request.EmailVerification dto) {
        String storedCode = (String) redisStore.get("email:verification:" + dto.getEmail(), "verificationCode");

        checkKeyStatus(storedCode, dto.getVerifyCode());

        appUserService.updateUserEmail(dto.getEmail());
    }

    /**
     * SNS 사용자의 이메일 설정 전, 인증코드 유효성 검사를 하고 SNS 사용자 유효성 검사 및 이메일을 설정하는 서비스를 호출합니다.
     * @param dto (sns 아이디, 이메일, 인증코드)
     */
    public Long verifyEmailCodeWithSetSnsEmail(MailDto.Request.SetSnsEmail dto) {
        String storedCode = (String) redisStore.get("email:verification:" + dto.getEmail(), "verificationCode");

        checkKeyStatus(storedCode, dto.getVerifyCode());

        return snsService.setSnsUserEmail(dto.getSnsId(), dto.getEmail());
    }
}
