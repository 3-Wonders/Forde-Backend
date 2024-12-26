//package com.project.forum.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class WebSecurityConfig {
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .headers((headerConfig) -> headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
//                .formLogin(AbstractHttpConfigurer::disable);
//
//        return http.build();
//    }
//}
