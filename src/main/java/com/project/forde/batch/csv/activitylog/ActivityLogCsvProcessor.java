package com.project.forde.batch.csv.activitylog;

import com.project.forde.batch.csv.CSVLogDto;
import com.project.forde.batch.csv.LogType;
import com.project.forde.entity.ActivityLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActivityLogCsvProcessor implements ItemProcessor<ActivityLog, CSVLogDto> {
    @Override
    public CSVLogDto process(ActivityLog item) {
        CSVLogDto csvLogDto = new CSVLogDto(
                item.getUser().getUserId(),
                item.getBoard().getBoardId(),
                LogType.toLogType(item.getLogType().name()),
                item.getDuration(),
                item.getRevisitCount(),
                item.getCreatedTime().toString()
        );

        log.info("csvLogDto: {}", csvLogDto);
        return csvLogDto;
    }
}
