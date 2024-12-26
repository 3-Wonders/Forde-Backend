package com.project.forde.service;

import com.project.forde.dto.FileDto;
import com.project.forde.dto.dummyImage.DummyImageDto;
import com.project.forde.entity.DummyImage;
import com.project.forde.mapper.DummyImageMapper;
import com.project.forde.repository.DummyImageRepository;
import com.project.forde.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DummyImageService {
    private final DummyImageRepository dummyImageRepository;

    private final FileStore fileStore;

    public DummyImageDto.Response.Image createImage(final MultipartFile image) {
        FileDto file = fileStore.storeFile("dummies/", image);
        DummyImage dummyImage = dummyImageRepository.save(DummyImageMapper.INSTANCE.toEntity(file));

        return DummyImageMapper.INSTANCE.toImage(dummyImage);
    }
}
