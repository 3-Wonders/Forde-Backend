package com.project.forde.util;

import com.project.forde.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserStore {
    private final RedisStore redisStore;

    /**
     * User ID를 가져오는 메소드
     * @return user_id
     */
    public Long getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String sessionId = request.getSession().getId();
        log.info("In GetCookie, sessionId: {}, agent : {}, IP : {}", sessionId, request.getHeader("User-Agent"), request.getRemoteAddr());

        if (sessionId == null) {
            return null;
        }

        Object userId = redisStore.get("spring:session:sessions:" + sessionId, "sessionAttr:userId");
        return userId == null ? null : Long.parseLong(userId.toString());
    }
}
