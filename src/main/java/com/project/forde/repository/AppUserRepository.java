package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUserId(Long userId);
    Optional<AppUser> findByEmail(String email);
    AppUser findByNickname(String nickname);
}
