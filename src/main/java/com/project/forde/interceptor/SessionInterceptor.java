package com.project.forde.interceptor;

import com.project.forde.util.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {
    private static final int EXTENDED_SESSION_TIME = 60 * 30; // 30 min

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        HttpSession session = request.getSession(false);
        Cookie[] cookies = request.getCookies();

        if (session != null) {
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (Constants.cookieName.equals(cookie.getName())) {
                        session.setMaxInactiveInterval(EXTENDED_SESSION_TIME);
                        cookie.setMaxAge(EXTENDED_SESSION_TIME);
                        cookie.setHttpOnly(true);
                        cookie.setSecure(true);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }
        }
        return true;
    }
}