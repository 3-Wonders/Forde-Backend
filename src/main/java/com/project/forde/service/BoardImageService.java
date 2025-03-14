package com.project.forde.service;

import com.project.forde.annotation.UserVerify;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardImageService {
    private final BoardImageRepository boardImageRepository;

    private final FileStore fileStore;
    private final BoardImageMapper boardImageMapper;

    @UserVerify
    public DummyImageDto.Response.Image createImage(final MultipartFile image) {
        FileDto file = null;
        BoardImage dummyImage = null;

        try {
            file = fileStore.storeFile(ImagePathEnum.BOARD.getPath(), image);
            dummyImage = boardImageRepository.save(boardImageMapper.toEntityWithoutBoard(file));
        } catch (Exception e) {
            if (file != null) {
                throw new FileUploadException(file.getStorePath());
            }
        }

        return boardImageMapper.toImage(dummyImage);
    }

    public<T> void createImages(T entity, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
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

        boardImageRepository.saveAll(dummyImages);
    }

    /**
     * 이미지 ID 목록을 비교하여, 추가할 이미지와 삭제할 이미지를 찾아서 처리한다.
     * @param entity Board 또는 Draft
     * @param newImageIds 새로운 이미지 ID 목록
     * @param <T> Board 또는 Draft
     */
    public<T> void updateDiffImages(T entity, List<Long> newImageIds) {
        if (newImageIds == null || newImageIds.isEmpty()) {
            return;
        }

        List<BoardImage> boardImages = null;

        if (entity instanceof Board board) {
            boardImages = boardImageRepository.findAllByBoard(board);
        } else if (entity instanceof Draft draft) {
            boardImages = boardImageRepository.findAllByDraft(draft);
        } else {
            log.error("Entity is not Board or Draft");
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Set<Long> existingIdSet = boardImages.stream().map(BoardImage::getImageId).collect(Collectors.toSet());
        Set<Long> newIdSet = new HashSet<>(newImageIds);

        // 새로운 이미지 ID와 기존 이미지 ID를 비교하여, 삭제할 이미지를 찾아서 삭제한다.
        List<BoardImage> deleteImages = boardImages.stream().filter(
                boardImage -> !newIdSet.contains(boardImage.getImageId())
        ).toList();

        if (!deleteImages.isEmpty()) {
            boardImageRepository.deleteAllInBatch(deleteImages);
        }

        // 새로운 이미지 ID와 기존 이미지 ID를 비교하여, 추가할 이미지를 찾아서 추가한다.
        HashSet<Long> diffSet = new HashSet<>(newIdSet);
        diffSet.removeAll(existingIdSet);

        if (!diffSet.isEmpty()) {
            List<BoardImage> dummyImages = boardImageRepository.findAllByImageIdInAndBoardIsNull(diffSet.stream().toList());

            if (dummyImages.size() != diffSet.size()) {
                throw new CustomException(ErrorCode.BAD_REQUEST_IMAGE);
            }

            for (BoardImage image : dummyImages) {
                if (entity instanceof Board board) {
                    image.setBoard(board);
                } else {
                    Draft draft = (Draft) entity;
                    image.setDraft(draft);
                }
            }

            boardImageRepository.saveAll(dummyImages);
        }

        // TODO: Kafka 또는 무언가를 사용하여 Topic을 발생시키고, 삭제할 이미지를 모아서 삭제하도록 요청 (fileStore.deleteFile())
        deleteImages.forEach(boardImage -> fileStore.deleteFile(boardImage.getImagePath()));
    }
}
