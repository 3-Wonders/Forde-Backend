package com.project.forde.util;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CustomTimestamp {
    private LocalDateTime timestamp;

    public CustomTimestamp() {
        this.timestamp = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public String getLastDay() {
        return this.timestamp.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getLastMonth() {
        return this.timestamp.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    @Override
    public String toString() {
        return this.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
