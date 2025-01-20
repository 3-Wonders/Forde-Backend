package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUserId(Long userId);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByNickname(String nickname);
  
    List<AppUser> findAllByUserIdIn(List<Long> userId);
    
    Page<AppUser> findAllByNicknameContaining(Pageable pageable, String nickname);
}
