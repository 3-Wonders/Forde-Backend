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
    }
}
