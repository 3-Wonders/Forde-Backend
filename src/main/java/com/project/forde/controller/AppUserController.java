package com.project.forde.controller;

import com.project.forde.dto.RequestLoginDto;
import com.project.forde.dto.RequestUpdateProfileDto;
import com.project.forde.service.AppUserService;
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

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getOtherUser(@PathVariable(value = "user_id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getOtherUser(userId));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> publicLogin(@Valid @RequestBody RequestLoginDto dto) {
        // NOTE: Validation 에러 사용법을 보여주기 위해 미리 작성된 API입니다.
        // TODO: 1. 회원가입을 먼저 처리하세요.
        // TODO: 1. 로그인 로직을 작성하세요.
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImg(@Valid @ModelAttribute RequestUpdateProfileDto dto) {
        // NOTE: Validation 에러 사용법을 보여주기 위해 미리 작성된 API입니다.
        // TODO: 1. 사용자 인증(세션)을 먼저 처리 하세요.
        // TODO: 2. Firebase Storage을 이용하여 기존 이미지를 삭제하고, 새로운 이미지를 업로드하는 로직을 작성하세요.
        return ResponseEntity.noContent().build();
    }
}
