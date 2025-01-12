package com.project.forde.controller;

import com.project.forde.dto.RequestLoginDto;
import com.project.forde.dto.RequestMailCompareDto;
import com.project.forde.dto.RequestMailDto;
import com.project.forde.dto.RequestUpdateProfileDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.service.AppUserService;
import com.project.forde.service.MailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@ResponseBody
public class AppUserController {
    private final AppUserService appUserService;
    private final MailService mailService;

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getOtherUser(@PathVariable(value = "user_id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getOtherUser(userId));
    }

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImg(@Valid @ModelAttribute RequestUpdateProfileDto dto) {
        // NOTE: Validation 에러 사용법을 보여주기 위해 미리 작성된 API입니다.
        // TODO: 1. 사용자 인증(세션)을 먼저 처리 하세요.
        // TODO: 2. Firebase Storage을 이용하여 기존 이미지를 삭제하고, 새로운 이미지를 업로드하는 로직을 작성하세요.
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "")
    public ResponseEntity<?> create(@Valid @RequestBody AppUserDto.Request dto) {
        appUserService.createAppUser(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value="/login")
    public ResponseEntity<?> publicLogin(@Valid @RequestBody RequestLoginDto dto, final HttpServletRequest request) {
        Long userId = appUserService.login(dto);
        final HttpSession session = request.getSession();

        session.setAttribute("userId", userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/verify")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody RequestMailDto dto) {
        mailService.sendEmail(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/verify/compare")
    public ResponseEntity<?> compareEmail(@RequestBody RequestMailCompareDto dto, final HttpServletRequest request) {
        Long userId = mailService.compareEmail(dto);
        final HttpSession session = request.getSession();

        session.setAttribute("userId", userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/intro")
    public ResponseEntity<?> getIntroUser(final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getIntroUser(request));
    }

    @GetMapping("")
    public ResponseEntity<?> getMyInfoUser(final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getMyInfo(request));
    }

    @GetMapping("/sample")
    public ResponseEntity<?> sampleTest(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getRedisUserId(request));
    }

    @GetMapping("account")
    public ResponseEntity<?> getAccount(final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getAccount(request));
    }
}
