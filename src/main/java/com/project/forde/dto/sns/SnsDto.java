package com.project.forde.dto.sns;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SnsDto {
    public static class Request {
        @Getter
        public static class linkAccountSns {
            @NotBlank(message = "snsId를 입력해주세요.")
            private String snsId;
            @NotBlank(message = "sns 유형을 입력해주세요.")
            private String snsKind;
        }
    }

    public static class Response {

        @Getter
        @Setter
        @AllArgsConstructor
        public static class connectedStatus {
            private Integer snsKind;
            private String snsName;
            @JsonProperty("isConnect")
            private boolean isConnect;

            @Override
            public String toString() {
                return "connectedStatus{" +
                        "snsKind=" + snsKind +
                        ", snsName='" + snsName + '\'' +
                        ", isConnect=" + isConnect +
                        '}';
            }
        }
    }
}
