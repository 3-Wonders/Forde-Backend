package com.project.forde.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.project.forde.dto.FileDto;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class FileStore {
    private final Bucket bucket;
    private String defaultThumbnailPath;
    private String defaultProfilePath;

    public FileStore(Bucket bucket) {
        this.bucket = bucket;
        this.defaultThumbnailPath = initialDefaultThumbnailPath();
        this.defaultProfilePath = initialDefaultProfilePath();
    }

    private String getStoreFileName(String ext) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String getExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String initialDefaultThumbnailPath() {
        return defaultThumbnailPath = String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                URLEncoder.encode("board/default.png", StandardCharsets.UTF_8)
        );
    }

    private String initialDefaultProfilePath() {
        return defaultProfilePath = String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                URLEncoder.encode("profile/default.png", StandardCharsets.UTF_8)
        );
    }

    public String getDefaultProfilePath(String storePath) {
        if (defaultProfilePath == null || storePath == null || storePath.isEmpty()) {
            initialDefaultProfilePath();
        }

        return defaultProfilePath;
    }

    public String getDefaultThumbnailPath(String storePath) {
        if (defaultThumbnailPath == null || storePath == null || storePath.isEmpty()) {
            initialDefaultThumbnailPath();
        }

        return defaultThumbnailPath;
    }

    public FileDto storeFile(String directoryPath, MultipartFile file) {
        log.info("store file: [path : {}, name : {}]", directoryPath, file.getOriginalFilename());

        if (file.isEmpty()) {
            log.error("Failed to store empty file because file empty. [path : {}, name : {}]", directoryPath, file.getOriginalFilename());
            throw new CustomException(ErrorCode.ERROR_FILE_UPLOAD);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            log.error("Failed to store file because can not found extension. [path : {}, name : {}]", directoryPath, file.getOriginalFilename());
            throw new CustomException(ErrorCode.ERROR_FILE_UPLOAD);
        }

        String ext = getExtension(originalFilename);
        String storeFileName = getStoreFileName(ext);
        String type = file.getContentType();
        String storeFilePath = directoryPath + storeFileName;
        long size = file.getSize();

        if (bucket.get(storeFileName) != null) {
            log.error("Failed to store file because file already exists. [path : {}, name : {}]", directoryPath, file.getOriginalFilename());
            throw new CustomException(ErrorCode.ERROR_FILE_UPLOAD);
        }

        try {
            bucket.create(storeFilePath, file.getBytes(), type);
        } catch (IOException e) {
            log.error("Failed to store file because can not read file. [path : {}, name : {}]", directoryPath, file.getOriginalFilename());
            throw new CustomException(ErrorCode.ERROR_FILE_UPLOAD);
        } catch (Exception e) {
            log.error("Failed to store file because can not create file. [path : {}, name : {}, reason : {}]", directoryPath, file.getOriginalFilename(), e.getMessage());
            throw new CustomException(ErrorCode.ERROR_FILE_UPLOAD);
        }

        return FileDto.builder()
                .storeFileName(storeFileName)
                .originalFileName(originalFilename)
                .storePath(storeFilePath)
                .size(size)
                .extension(type)
                .build();
    }

    public void deleteFile(String storePath) {
        log.info("delete file: [name : {}]", storePath);

        try {
            Blob file = bucket.get(storePath);

            if (file == null) {
                log.error("Failed to delete file because file not found. [name : {}]", storePath);
                return;
            }

            file.delete();
        } catch (Exception e) {
            log.error("Failed to delete file because can not delete file. [path : {}, reason : {}]", storePath, e.getMessage());
        }
    }
}
