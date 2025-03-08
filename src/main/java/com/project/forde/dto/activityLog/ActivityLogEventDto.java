package com.project.forde.dto.activityLog;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;

public class ActivityLogEventDto {

    public static class Create {
        public record Search(
                AppUser user,
                String keyword
        ) {}

        public record Revisit(
                AppUser user,
                Board board
        ) {}
    }
}
