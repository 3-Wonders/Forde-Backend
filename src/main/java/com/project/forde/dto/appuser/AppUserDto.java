package com.project.forde.dto.appuser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class AppUserDto {
    public static  class Response {

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Intro {
            private Long userId;
            private String nickname;
            private String profilePath;
        }
    }
}
