package com.project.forde.config;

import com.project.forde.service.SnsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final SnsService snsService;

    public WebSecurityConfig(SnsService snsService) {
        this.snsService = snsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable
                )
                .headers((headerConfig) ->
                        headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .successHandler(oAuth2LoginSuccessHandler())  // 성공 시 처리
                                .failureHandler(oAuth2LoginFailureHandler())  // 실패 시 처리
                );
        return http.build();
    }

    // OAuth2 로그인 성공 후 처리
    public AuthenticationSuccessHandler oAuth2LoginSuccessHandler() {
        return (request, response, authentication) -> {
            try {
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                String socialType = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

                snsService.auth(oAuth2User, socialType);

                String redirectUrl = "http://localhost:5173/callback?success=true";

                request.getSession().setAttribute("userId", oAuth2User.getAttribute("sub"));
                response.sendRedirect(redirectUrl);

            } catch (Exception e) {
                request.getSession().invalidate();
                String encodedMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                String errorRedirectUrl = "http://localhost:5173/callback?success=false&message=" + encodedMessage;
                response.sendRedirect(errorRedirectUrl);
            }
        };
    }

    // OAuth2 로그인 실패 후 처리
    public AuthenticationFailureHandler oAuth2LoginFailureHandler() {
        return (request, response, exception) -> {
            request.getSession().invalidate();
            String encodedMessage = URLEncoder.encode("나중에 다시 시도해주세요.", StandardCharsets.UTF_8);
            String redirectUrl = "http://localhost:5173/callback?success=false&message=" + encodedMessage;
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
