package com.project.forde.mapper;

import com.project.forde.util.CustomTimestamp;
import org.mapstruct.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Qualifier
@interface CustomTimestampTranslator { }
@Qualifier @interface MapCreatedTime { }

@Component
@CustomTimestampTranslator
public class CustomTimestampMapper {
    @MapCreatedTime
    public String mapCreatedTime(LocalDateTime createdTime) {
        CustomTimestamp timestamp = new CustomTimestamp();
        timestamp.setTimestamp(createdTime);

        return String.valueOf(timestamp);
    }
}
