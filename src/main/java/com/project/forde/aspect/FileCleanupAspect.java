package com.project.forde.aspect;

import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.exception.FileUploadException;
import com.project.forde.util.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class FileCleanupAspect {
    private final FileStore fileStore;

    public FileCleanupAspect(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    @AfterThrowing(
            pointcut = "execution(* com.project.forde.service.BoardService.*(..)) ||" +
                    "execution(* com.project.forde.service.FileService.*(..)) ||" +
                    "execution(* com.project.forde.service.BoardImageService.*(..)) ||" +
                    "execution(* com.project.forde.service.DraftService.*(..))",
            throwing = "ex"
    )
    public void handleCleanup(Exception ex) throws Exception {
        if (ex instanceof FileUploadException fileUploadException) {
            log.error("File upload failed. Deleting file: [path : {}]", fileUploadException.getFilePath());
            String filePath = fileUploadException.getFilePath();

            if (filePath != null) {
                fileStore.deleteFile(filePath);
            }

            throw new CustomException(ErrorCode.ERROR_FILE_UPLOAD);
        }

        throw ex;
    }
}
