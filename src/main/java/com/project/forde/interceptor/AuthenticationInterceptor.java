package com.project.forde.interceptor;

import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.util.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        Cookie[] cookies = request.getCookies();

        if (session == null) {
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (Constants.cookieName.equals(cookie.getName())) {
                        cookie.setMaxAge(0);
                        cookie.setHttpOnly(true);
                        cookie.setSecure(true);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }

            throw new CustomException(ErrorCode.BLANK_COOKIE);
        }
        if (session.getAttribute("userId") == null) {
            session.invalidate();
            throw new CustomException(ErrorCode.EXPIRED_COOKIE);
        }

        return true;
    }
}
