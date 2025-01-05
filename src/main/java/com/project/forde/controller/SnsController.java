package com.project.forde.controller;

import com.project.forde.service.SnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@ResponseBody
public class SnsController {
    private final SnsService snsService;

    @GetMapping("/google")
    public ResponseEntity<?> handleGoogleAuth() {
        // 구글 로그인 페이지로 리다이렉트
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/oauth2/authorization/google")
                .build();
    }
}
