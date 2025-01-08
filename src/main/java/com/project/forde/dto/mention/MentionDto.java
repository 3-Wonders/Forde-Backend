package com.project.forde.dto.mention;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MentionDto {
    public static class Response {
        @Getter
        @AllArgsConstructor
        public static class Mention {
            private Long userId;
            private String nickname;
        }
    }
}
