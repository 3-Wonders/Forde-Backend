package com.project.forde.service;

import com.project.forde.dto.FileDto;
import com.project.forde.entity.Board;
import com.project.forde.entity.Draft;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.exception.FileUploadException;
import com.project.forde.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileStore fileStore;

    /**
     * 썸네일을 처리하고 저장하는 메서드
     *
     * @param thumbnail    썸네일 파일
     * @param path         저장 경로
     * @param entity       엔티티 (Board, Draft)
     * @param saveFunction 저장 함수
     * @param <T>          엔티티 타입
     * @throws FileUploadException 파일 업로드 실패 (AOP에서 처리)
     */
    public<T> void processThumbnailAndSave(
            MultipartFile thumbnail,
            String path,
            T entity,
            Consumer<T> saveFunction
    ) throws FileUploadException {
        FileDto file = null;

        try {
            if (thumbnail != null) {
                file = fileStore.storeFile(path, thumbnail);

                if (entity instanceof Board board) {
                    board.setThumbnailPath(file.getStorePath());
                    board.setThumbnailSize(file.getSize());
                    board.setThumbnailType(file.getExtension());
                } else if (entity instanceof Draft draft) {
                    draft.setThumbnailPath(file.getStorePath());
                    draft.setThumbnailSize(file.getSize());
                    draft.setThumbnailType(file.getExtension());
                }
            }

            saveFunction.accept(entity);
        } catch (Exception e) {
            if (file != null) {
                throw new FileUploadException(file.getStorePath());
            }

            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
