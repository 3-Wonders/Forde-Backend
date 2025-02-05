package com.project.forde.batch.csv;

public enum LogType {
    SEARCH,
    REVISIT,
    DURATION,
    LIKE,
    COMMENT,
    VIEW,
    ;

    public static LogType toLogType(String logType) {
        return LogType.valueOf(logType.toUpperCase());
    }
}
