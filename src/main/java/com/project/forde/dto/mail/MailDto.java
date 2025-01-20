package com.project.forde.dto.mail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.project.forde.dto.sns.SnsDto;
import com.project.forde.dto.tag.TagDto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class MailDto {
    public static class Request {

        @Getter
        public static class send {
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "유효한 이메일을 입력해주세요.")
            private final String email;

            @JsonCreator
            public send(String email) {
                this.email = email;
            }
        }

        @Getter
        @AllArgsConstructor
        public static class compareVerifyCode {
            @NotNull(message = "이메일을 입력해주세요.")
            @Email(message = "유효한 이메일을 입력해주세요.")
            private String email;
            @NotNull(message = "인증 코드를 입력해주세요.")
            private String verifyCode;
        }

        @Getter
        public static class compareRandomKey {
            @NotBlank(message = "이메일을 입력해주세요.")
            private final String randomKey;

            @JsonCreator
            public compareRandomKey(String randomKey) {
                this.randomKey = randomKey;
            }
        }
    }
}
