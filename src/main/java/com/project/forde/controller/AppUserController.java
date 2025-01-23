package com.project.forde.controller;

import com.project.forde.dto.RequestLoginDto;
import com.project.forde.dto.mail.MailDto;
import com.project.forde.dto.RequestUpdateProfileDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.service.AppUserService;
import com.project.forde.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> create(@Valid @RequestBody AppUserDto.Request.Signup dto) {
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
    public ResponseEntity<?> sendVerificationCode(@Valid @RequestBody MailDto.Request.Send dto) {
        mailService.sendVerificationCode(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/verify/compare")
    public ResponseEntity<?> verifyEmail(@RequestBody MailDto.Request.EmailVerification dto, final HttpServletRequest request) {
        Long userId = mailService.verifyEmail(dto);
        final HttpSession session = request.getSession();

        session.setAttribute("userId", userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/verify/compare/password")
    public ResponseEntity<?> verifyEmailPassword(@RequestBody MailDto.Request.EmailVerification dto) {
        mailService.verifyEmailPassword(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/password/randomkey")
    public ResponseEntity<?> verifyRandomKey(@RequestBody MailDto.Request.RandomKeyVerification dto) {
        mailService.verifyRandomKey(dto.getRandomKey());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/password")
    public ResponseEntity<?> updatePassword(@RequestBody AppUserDto.Request.UpdatePassword dto) {
        appUserService.updatePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/intro")
    public ResponseEntity<?> getIntroUser() {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getIntroUser());
    }

    @GetMapping("/{user_id}/news")
    public ResponseEntity<?> getUserNews(
            @PathVariable(value = "user_id") Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getUserBoard(userId, 'N', page, count));
    }

    @GetMapping("/{user_id}/board")
    public ResponseEntity<?> getUserBoards(
            @PathVariable(value = "user_id") Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getUserBoard(userId, 'B', page, count));
    }

    @GetMapping("/{user_id}/question")
    public ResponseEntity<?> getUserQuestions(
            @PathVariable(value = "user_id") Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getUserBoard(userId, 'Q', page, count));
    }

    @GetMapping("/{user_id}/like")
    public ResponseEntity<?> getUserLikeBoards(
            @PathVariable(value = "user_id") Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getUserLikeBoard(userId, page, count));
    }

    @GetMapping("")
    public ResponseEntity<?> getMyInfoUser() {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getMyInfo());
    }


    @GetMapping("account")
    public ResponseEntity<?> getAccount() {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getAccount());
    }

    @GetMapping(value = "/mention")
    public ResponseEntity<?> getSearchUsersNickname(
            @RequestParam(value = "nickname", required = false, defaultValue = "") final String nickname,
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getSearchUserNickname(page, count, nickname));
    }

    @PatchMapping(value = "/sns/setting")
    public ResponseEntity<?> updateSocialSetting(@RequestBody @Valid AppUserDto.Request.UpdateSocialSetting dto) {
        appUserService.updateSocialSetting(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/notification")
    public ResponseEntity<?> updateNotificationSetting(@RequestBody @Valid AppUserDto.Request.UpdateNotificationSetting dto) {
        appUserService.updateNotificationSetting(dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "")
    public ResponseEntity<?> updateMyInfo(@RequestBody @Valid AppUserDto.Request.UpdateMyInfo dto) {
        appUserService.updateMyInfo(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "")
    public ResponseEntity<?> removeUser(final HttpServletRequest request) {
        appUserService.removeUser(request);
        return ResponseEntity.noContent().build();
    }
}
