package com.project.forde.dto.sns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SnsDto {
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
