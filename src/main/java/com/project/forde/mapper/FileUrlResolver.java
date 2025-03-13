package com.project.forde.mapper;

import com.project.forde.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUrlResolver {
    private final FileStore fileStore;

    @Named("getDefaultProfilePath")
    public String getDefaultProfilePath(String storePath) {
        return fileStore.getDefaultProfilePath(storePath);
    }

    @Named("getDefaultThumbnailPath")
    public String getDefaultThumbnailPath(String storePath) {
        return fileStore.getDefaultThumbnailPath(storePath);
    }
}
