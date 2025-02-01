package com.project.forde.repository;

import com.project.forde.entity.Follow;
import com.project.forde.entity.composite.FollowPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, FollowPK> {
    Optional<Follow> findByFollowPK(FollowPK followPK);
}
