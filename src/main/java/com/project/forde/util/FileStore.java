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

    public FileStore(Bucket bucket) {
        this.bucket = bucket;
    }

    private String getStoreFileName(String ext) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String getExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String getPath(String storePath) {
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                URLEncoder.encode(storePath, StandardCharsets.UTF_8)
        );
    }

    public String getProfilePath(String storePath) {
        log.info("get default thumbnail path: [storePath : {}]", storePath);

        boolean isNullOrEmpty = storePath == null || storePath.isEmpty();
        if (isNullOrEmpty) {
            return getPath("profile/default.png");
        }

        boolean isHttps = storePath.startsWith("https://");
        if (isHttps) {
            return storePath;
        }

        return getPath(storePath);
    }

    public String getThumbnailPath(String storePath) {
        log.info("get default thumbnail path: [storePath : {}]", storePath);
        if (storePath == null || storePath.isEmpty()) {
            return getPath("board/default.png");
        }

        return getPath(storePath);
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
