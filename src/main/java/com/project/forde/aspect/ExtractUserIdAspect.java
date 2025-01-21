package com.project.forde.aspect;

import com.project.forde.util.UserStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ExtractUserIdAspect {
    private final UserStore userStore;

    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

    @Around("@annotation(com.project.forde.annotation.ExtractUserId)")
    public Object verifyUser(ProceedingJoinPoint joinPoint) throws Throwable {
        Long userId = userStore.getUserId();
        userIdThreadLocal.set(userId);

        try {
            log.info("In ExtractUserIdAspect, userId: {}", userId);
            return joinPoint.proceed();
        } finally {
            userIdThreadLocal.remove();
        }
    }

    public static Long getUserId() {
        return userIdThreadLocal.get();
    }
}
