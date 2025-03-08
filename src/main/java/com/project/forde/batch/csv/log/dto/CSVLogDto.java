package com.project.forde.batch.csv.log.dto;


import com.project.forde.batch.csv.log.LogType;

public record CSVLogDto(
        Long userId,
        Long boardId,
        LogType logType,
        Long duration,
        Long revisit,
        String date
) {
    @Override
    public String toString() {
        return "CSVLogDto{" +
                "userId=" + userId +
                ", boardId=" + boardId +
                ", logType=" + logType +
                ", duration=" + duration +
                ", revisit=" + revisit +
                ", date='" + date + '\'' +
                '}';
    }
}