package com.project.forde.dto.appuser;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

public class AppUserDto {

    @Getter
    public static class Request {
        @NotNull(message = "이메일을 입력해주세요.")
        @Size(max = 60, message = "이메일은 60자 이하여야 합니다.")
        private String email;

        @NotNull(message = "비밀번호를 입력해주세요.")
        @Size(max = 300, message = "비밀번호는 300자 이하여야 합니다.")
        private String password;

        @NotNull(message = "닉네임을 입력해주세요.")
        @Size(max = 300, message = "닉네임은 10자 이하여야 합니다.")
        private String nickname;

        @NotNull(message = "일반 알림 여부를 체크해주세요.")
        private Boolean isEnableNotification;

        @NotNull(message = "이벤트성 알림 여부를 체크해주세요.")
        private Boolean isEnableEvent;
    }

    public static class Response {

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
