package com.project.forde.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileDto {
    private String originalFileName;
    private String storeFileName;
    private String storePath;
    private String extension;
    private long size;

    @Builder
    public FileDto(String originalFileName, String storeFileName, String storePath, String extension, long size) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
        this.storePath = storePath;
        this.extension = extension;
        this.size = size;
    }
}
