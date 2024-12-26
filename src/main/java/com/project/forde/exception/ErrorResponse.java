package com.project.forde.exception;

import com.project.forde.util.CustomTimestamp;
import lombok.Builder;
import lombok.Getter;

import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private final String timestamp = new CustomTimestamp().toString();
    private final int statusCode;
    private final String statusName;
    private final String error;
    private final String errorCode;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .statusCode(errorCode.getStatus().value())
                        .statusName(errorCode.getStatus().name())
                        .error(errorCode.name())
                        .errorCode(errorCode.getErrorCode())
                        .message(errorCode.getMessage())
                        .build()
                );
    }
}