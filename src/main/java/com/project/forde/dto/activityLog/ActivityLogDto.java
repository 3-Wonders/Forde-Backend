package com.project.forde.dto.activityLog;

import jakarta.validation.constraints.NotNull;

public class ActivityLogDto {
    public static class Request {
        public record Create(
                @NotNull(message = "Board Id는 필수입니다.")
                Long boardId,
                @NotNull(message = "Duration은 필수입니다.")
                Long duration
        ) {}
    }
}
