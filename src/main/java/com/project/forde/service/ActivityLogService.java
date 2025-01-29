package com.project.forde.service;

import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.activityLog.ActivityLogDto;
import com.project.forde.dto.activityLog.ActivityLogEventDto;
import com.project.forde.entity.ActivityLog;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.ActivityLogMapper;
import com.project.forde.repository.ActivityLogRepository;
import com.project.forde.repository.BoardRepository;
import com.project.forde.type.LogTypeEnum;
import com.project.forde.util.CustomTimestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityLogService {
    private final AppUserService appUserService;

    private final ActivityLogRepository activityLogRepository;
    private final BoardRepository boardRepository;

    @UserVerify
    public void publishDuration(ActivityLogDto.Request.Create request) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);
        Board board = boardRepository.findByBoardId(request.boardId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        if (board.getUploader().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.CAN_NOT_LOG_MY_BOARD);
        }

        ActivityLog activityLog = ActivityLogMapper.INSTANCE.toDurationEntity(
                user,
                board,
                request.duration()
        );

        log.info(
                "Publish duration log: user={}, board={}, duration={}",
                user.getUserId(),
                board.getBoardId(),
                request.duration()
        );

        activityLogRepository.save(activityLog);
    }

    public void publishSearch(ActivityLogEventDto.Create.Search request) {
        ActivityLog entity = activityLogRepository.findFirstByUserAndKeyword(
                request.user(),
                request.keyword()
        ).orElse(null);

        if (entity != null) {
            entity.setCreatedTime(new CustomTimestamp().getTimestamp());
            activityLogRepository.save(entity);
            return;
        }

        ActivityLog activityLog = ActivityLogMapper.INSTANCE.toSearchEntity(
                request.user(),
                request.keyword()
        );

        log.info(
                "Publish search log: user={}, keyword={}",
                request.user().getUserId(),
                request.keyword()
        );

        activityLogRepository.save(activityLog);
    }

    public void publishRevisitLog(ActivityLogEventDto.Create.Revisit request) {
        ActivityLog entity = activityLogRepository.findFirstByUserAndBoardAndLogType(
                request.user(),
                request.board(),
                LogTypeEnum.REVISIT
        ).orElse(null);

        log.info(
                "Create revisit log: user={}, board={}, revisitCount={}",
                request.user().getUserId(),
                request.board(),
                entity != null ? entity.getRevisitCount() : 0
        );

        if (entity != null) {
            entity.setRevisitCount(entity.getRevisitCount() + 1);
            activityLogRepository.save(entity);
            return;
        }

        ActivityLog activityLog = ActivityLogMapper.INSTANCE.toRevisitEntity(
                request.user(),
                request.board(),
                1L
        );

        activityLogRepository.save(activityLog);
    }
}
