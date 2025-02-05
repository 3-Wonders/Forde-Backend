package com.project.forde.batch.csv.activitylog;

import com.project.forde.entity.ActivityLog;
import com.project.forde.type.LogTypeEnum;
import com.project.forde.util.CustomTimestamp;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityLogCSVReader implements ItemReader<ActivityLog> {
    private final EntityManagerFactory entityManagerFactory;
    private JpaPagingItemReader<ActivityLog> activityLogReader;

    @PostConstruct
    public void init() {
        log.info("ActivityCSVReader.init() 호출");
        activityLogReader = createActivityLogReader();
        this.activityLogReader.open(new ExecutionContext());
    }

    @Override
    public ActivityLog read() throws Exception {
        return activityLogReader.read();
    }

    private JpaPagingItemReader<ActivityLog> createActivityLogReader() {
        LocalDateTime interval = new CustomTimestamp().getTimestamp().minusMonths(3);

        return new JpaPagingItemReaderBuilder<ActivityLog>()
                .name("recommendActivityLogReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT log
                        FROM ActivityLog log
                        WHERE log.createdTime >= :interval
                            AND log.logType <> :logType
                        ORDER BY log.createdTime DESC
                        """)
                .parameterValues(Map.of("interval", interval, "logType", LogTypeEnum.SEARCH))
                .pageSize(100)
                .build();
    }
}
