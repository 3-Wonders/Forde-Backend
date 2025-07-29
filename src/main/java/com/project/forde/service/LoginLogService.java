package com.project.forde.service;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.LoginLog;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.LoginLogRepository;
import com.project.forde.util.CustomTimestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginLogService {
    private final LoginLogRepository loginLogRepository;
    private final AppUserRepository appUserRepository;

    public void saveLoginLog(Long userId) {
        log.info("Saving login log for userId = {}", userId);
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Optional<LoginLog> loginLog = loginLogRepository.findByUser(user);

        if (loginLog.isPresent()) {
            LoginLog loginLogEntity = loginLog.get();
            loginLogEntity.setLoggedInTime(new CustomTimestamp().getTimestamp());

            loginLogRepository.save(loginLogEntity);
        } else {
            loginLogRepository.save(
                    LoginLog.builder()
                            .user(user)
                            .build()
            );
        }
    }
}
