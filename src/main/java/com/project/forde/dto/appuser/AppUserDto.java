package com.project.forde.dto.appuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

public class AppUserDto {

    @Getter
    public static class Request {
        @NotBlank(message = "이메일을 작성해주세요.")
        @Size(max = 60, message = "이메일은 60자 이하여야 합니다.")
        private String email;

        @NotBlank(message = "비밀번호를 작성해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자 이내로 입력해야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "영문자, 숫자, 특수문자를 1개씩 포함해야 합니다.")
        private String password;

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
