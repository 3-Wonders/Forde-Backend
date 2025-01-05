package com.project.forde.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
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
                                .loginPage("/auth") // 기본 로그인 페이지를 /auth로 설정
                                .successHandler(oAuth2LoginSuccessHandler())  // 성공 시 처리
                                .failureHandler(oAuth2LoginFailureHandler())  // 실패 시 처리
                );
        return http.build();
    }

    // OAuth2 로그인 성공 후 처리
    public AuthenticationSuccessHandler oAuth2LoginSuccessHandler() {
        return (request, response, authentication) -> {
            // 로그인 성공 후 리다이렉트할 URL
            String redirectUrl = "http://localhost:5173/callback?success=true";
            response.sendRedirect(redirectUrl);
        };
    }

    // OAuth2 로그인 실패 후 처리
    public AuthenticationFailureHandler oAuth2LoginFailureHandler() {
        return (request, response, exception) -> {
            // 로그인 실패 시 리다이렉트할 URL
            String redirectUrl = "http://localhost:5173/callback?success=false&message=나중에 다시 시도해주세요.";
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
