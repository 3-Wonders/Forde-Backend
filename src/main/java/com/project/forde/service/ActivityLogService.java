package com.project.forde.service;

import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.activityLog.ActivityLogDto;
import com.project.forde.entity.ActivityLog;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.ActivityLogMapper;
import com.project.forde.repository.ActivityLogRepository;
import com.project.forde.repository.BoardRepository;
import com.project.forde.type.LogTypeEnum;
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
}
