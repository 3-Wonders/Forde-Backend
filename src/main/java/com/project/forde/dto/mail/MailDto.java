package com.project.forde.dto.mail;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.*;
import lombok.Getter;

public class MailDto {
    public static class Request {

        @Getter
        public static class Send {
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "유효한 이메일을 입력해주세요.")
            private final String email;

            @JsonCreator
            public Send(String email) {
                this.email = email;
            }
        }

        @Getter
        public static class EmailVerification {
            @NotNull(message = "이메일을 입력해주세요.")
            @Email(message = "유효한 이메일을 입력해주세요.")
            private String email;
            @NotNull(message = "인증 코드를 입력해주세요.")
            private String verifyCode;
        }

        @Getter
        public static class RandomKeyVerification {
            @NotBlank(message = "이메일을 입력해주세요.")
            private final String randomKey;

            @JsonCreator
            public RandomKeyVerification(String randomKey) {
                this.randomKey = randomKey;
            }
        }

        @Getter
        public static class UpdatePassword {
            @NotBlank(message = "비밀번호를 작성해주세요.")
            @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자 이내로 입력해야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "영문자, 숫자, 특수문자를 1개씩 포함해야 합니다.")
            private String password;

            @NotBlank(message = "랜덤키를 입력해주세요.")
            private String randomKey;
        }
    }
}
