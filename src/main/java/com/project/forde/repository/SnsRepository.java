package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnsRepository extends JpaRepository<Sns, String> {
    Optional<Sns> findBySnsId(String userId);
}
