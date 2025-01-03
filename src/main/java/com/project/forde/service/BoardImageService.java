package com.project.forde.service;

import com.project.forde.dto.FileDto;
import com.project.forde.dto.dummyImage.DummyImageDto;
import com.project.forde.entity.BoardImage;
import com.project.forde.mapper.BoardImageMapper;
import com.project.forde.repository.BoardImageRepository;
import com.project.forde.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BoardImageService {
    private final BoardImageRepository boardImageRepository;

    private final FileStore fileStore;

    public DummyImageDto.Response.Image createImage(final MultipartFile image) {
        FileDto file = fileStore.storeFile("board/", image);
        BoardImage dummyImage = boardImageRepository.save(BoardImageMapper.INSTANCE.toEntityWithoutBoard(file));

        return BoardImageMapper.INSTANCE.toImage(dummyImage);
    }
}
