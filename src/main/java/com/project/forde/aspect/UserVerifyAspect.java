package com.project.forde.aspect;

import com.project.forde.entity.AppUser;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
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
public class UserVerifyAspect {
    private final UserStore userStore;
    private final AppUserRepository appUserRepository;

    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

    @Around("@annotation(com.project.forde.annotation.UserVerify)")
    public Object verifyUser(ProceedingJoinPoint joinPoint) throws Throwable {
        Long userId = userStore.getUserId();
        if (userId == null) {
            throw new CustomException(ErrorCode.BLANK_COOKIE);
        }

        AppUser user = appUserRepository.findByUserId(Long.parseLong(userId.toString()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getDeleted()) {
            log.info("Deleted user: {}", user);
            throw new CustomException(ErrorCode.DELETED_USER);
        } else if (!user.getVerified()) {
            log.info("Not verified user: {}", user);
            throw new CustomException(ErrorCode.NOT_VERIFIED_USER);
        }

        userIdThreadLocal.set(userId);

        try {
            log.info("In UserVerifyAspect, userId: {}", userId);
            return joinPoint.proceed();
        } finally {
            userIdThreadLocal.remove();
        }
    }

    public static Long getUserId() {
        return userIdThreadLocal.get();
    }
}
