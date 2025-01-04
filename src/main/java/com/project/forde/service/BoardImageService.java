package com.project.forde.service;

import com.project.forde.dto.FileDto;
import com.project.forde.dto.dummyImage.DummyImageDto;
import com.project.forde.entity.Board;
import com.project.forde.entity.BoardImage;
import com.project.forde.entity.Draft;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.exception.FileUploadException;
import com.project.forde.mapper.BoardImageMapper;
import com.project.forde.repository.BoardImageRepository;
import com.project.forde.type.ImagePathEnum;
import com.project.forde.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardImageService {
    private final BoardImageRepository boardImageRepository;

    private final FileStore fileStore;

    public DummyImageDto.Response.Image createImage(final MultipartFile image) {
        FileDto file = null;
        BoardImage dummyImage = null;

        try {
            file = fileStore.storeFile(ImagePathEnum.BOARD.getPath(), image);
            dummyImage = boardImageRepository.save(BoardImageMapper.INSTANCE.toEntityWithoutBoard(file));
        } catch (Exception e) {
            if (file != null) {
                throw new FileUploadException(file.getStorePath());
            }
        }

        return BoardImageMapper.INSTANCE.toImage(dummyImage);
    }

    public<T> List<BoardImage> createImages(T entity, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<BoardImage> dummyImages = boardImageRepository.findAllByImageIdInAndBoardIsNull(ids);

        if (dummyImages.size() != ids.size()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_IMAGE);
        }

        for (BoardImage dummyImage : dummyImages) {
            if (entity instanceof Board board) {
                dummyImage.setBoard(board);
            } else if (entity instanceof Draft draft) {
                dummyImage.setDraft(draft);
            }
        }

        return dummyImages;
    }
}
