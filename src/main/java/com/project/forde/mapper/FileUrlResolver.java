package com.project.forde.mapper;

import com.project.forde.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUrlResolver {
    private final FileStore fileStore;

    @Named("getProfilePath")
    public String getProfilePath(String storePath) {
        return fileStore.getProfilePath(storePath);
    }

    @Named("getThumbnailPath")
    public String getThumbnailPath(String storePath) {
        return fileStore.getThumbnailPath(storePath);
    }
}
