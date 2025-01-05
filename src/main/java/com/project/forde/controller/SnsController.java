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
    public ResponseEntity<?> handleGoogleAuth(@RequestParam(value = "code", required = false) String code) {
        // code 파라미터가 없으면 로그인 페이지로 리다이렉트
        if (code == null) {
            return ResponseEntity.status(HttpStatus.FOUND)  // 302 리다이렉트 상태
                    .header("Location", "/oauth2/authorization/google")  // 구글 로그인 페이지로 리다이렉트
                    .build();
        }

        // code 파라미터가 있으면 후속 처리 (인증 코드로 액세스 토큰 요청)
//        String accessToken = googleAuthService.getAccessToken(code);
//        GoogleUser user = googleAuthService.getUserInfo(accessToken);

        // 사용자 정보 처리 (예: 로그인 후 세션 처리 등)
        return (ResponseEntity<?>) ResponseEntity.ok();  // 적절한 응답 반환 (여기서는 사용자 정보)
    }
}
