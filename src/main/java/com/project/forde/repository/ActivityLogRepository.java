package com.project.forde.repository;

import com.project.forde.entity.ActivityLog;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.type.LogTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Optional<ActivityLog> findFirstByUserAndBoardAndLogType(
            AppUser user,
            Board board,
            LogTypeEnum logType
    );

    Optional<ActivityLog> findFirstByUserAndKeyword(
            AppUser user,
            String keyword
    );
}
