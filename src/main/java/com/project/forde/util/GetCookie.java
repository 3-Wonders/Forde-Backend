package com.project.forde.util;

import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class GetCookie {

    private final RedisTemplate<String, Object> redisTemplate;

    public Long getUserId(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        System.out.println(sessionId);
        if (sessionId == null) {
            return null; // 세션 ID가 없으면 null 반환
        }
        System.out.println(redisTemplate.opsForList());

        // Redis에서 userId 가져오기
        String redisKey = "spring:session:sessions:" + sessionId;
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        Object userId = hashOperations.get(redisKey, "sessionAttr:userId");
        System.out.println("redisKey : " +redisKey);
        System.out.println(hashOperations);
        System.out.println("userId : " +userId);

        if (userId == null) {
            throw new CustomException(ErrorCode.BLANK_COOKIE);
        }

        return Long.parseLong(userId.toString());
    }
}
