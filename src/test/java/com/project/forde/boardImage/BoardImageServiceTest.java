package com.project.forde.boardImage;

import com.project.forde.AbstractTest;
import com.project.forde.dto.FileDto;
import com.project.forde.repository.BoardImageRepository;
import com.project.forde.service.BoardImageService;
import com.project.forde.util.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@ExtendWith({ MockitoExtension.class })
public class BoardImageServiceTest extends AbstractTest {
    @Mock
    private FileStore fileStore;
    @Mock
    private BoardImageRepository boardImageRepository;
    @InjectMocks
    private BoardImageService boardImageService;

    @Test
    @DisplayName("더미 이미지 추가")
    void createDummyImage() {
        Mockito.when(fileStore.storeFile(Mockito.anyString(), Mockito.any(MultipartFile.class)))
                .thenReturn(FileDto.builder()
                        .storeFileName("test.jpg")
                        .originalFileName("test.jpg")
                        .storePath("test.jpg")
                        .size(1024L)
                        .extension("image/jpeg")
                        .build()
                );
        Mockito.when(boardImageRepository.save(Mockito.any())).thenReturn(super.getBoardImage());

        boardImageService.createImage(super.getMockMultipartFile());

        Mockito.verify(boardImageRepository, Mockito.times(1)).save(ArgumentMatchers.any());
    }
}
