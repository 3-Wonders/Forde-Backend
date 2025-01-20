package com.project.forde.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.ERROR_FILE_UPLOAD;
    private final String filePath;
}
