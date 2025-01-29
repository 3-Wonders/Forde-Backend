package com.project.forde.event;

import com.project.forde.dto.activityLog.ActivityLogEventDto;
import com.project.forde.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogEventListener {
    private final ActivityLogService activityLogService;

    @Async
    @EventListener
    public void handleSearchEvent(ActivityLogEventDto.Create.Search event) {
        log.info("Search event: {}", event);
        activityLogService.publishSearch(event);
    }

    @Async
    @EventListener
    public void handleRevisitEvent(ActivityLogEventDto.Create.Revisit event) {
        log.info("Revisit event: {}", event);
        activityLogService.publishRevisitLog(event);
    }
}
