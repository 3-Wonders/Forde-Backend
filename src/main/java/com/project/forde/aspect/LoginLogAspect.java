package com.project.forde.aspect;

import com.project.forde.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginLogAspect {
    private final LoginLogService loginLogService;

    @AfterReturning(
            pointcut = """
            execution(* com.project.forde.service.AppUserService.login(..)) ||
            execution(* com.project.forde.service.SnsService.socialAuth(..))
            """,
            returning = "userId"
    )
    public void saveLoginLog(Long userId) {
        loginLogService.saveLoginLog(userId);
    }
}
