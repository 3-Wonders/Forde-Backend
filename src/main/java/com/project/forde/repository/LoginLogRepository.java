package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    Optional<LoginLog> findByUser(AppUser user);
}
