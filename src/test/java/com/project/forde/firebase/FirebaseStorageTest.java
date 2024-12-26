package com.project.forde.firebase;

import com.project.forde.dto.FileDto;
import com.project.forde.exception.CustomException;
import com.project.forde.util.FileStore;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
public class FirebaseStorageTest {
    @Mock
    private Bucket bucket;

    @Mock
    private Blob blob;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileStore firestore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        multipartFile = Mockito.mock(MultipartFile.class);
    }

    @Test
    @DisplayName("파일 저장 성공")
    void storeFile() throws IOException {
        byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01};

        Mockito.when(multipartFile.isEmpty()).thenReturn(false);
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        Mockito.when(multipartFile.getContentType()).thenReturn("image/jpeg");
        Mockito.when(multipartFile.getSize()).thenReturn(1024L);
        Mockito.when(multipartFile.getBytes()).thenReturn(imageContent);


        FileDto dto = firestore.storeFile("testDirectory/", multipartFile);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals("image/jpeg", dto.getExtension());
        Assertions.assertEquals(1024L, dto.getSize());
        Assertions.assertEquals("test.jpg", dto.getOriginalFileName());

        log.debug("Saving File path : {}", dto.getStorePath());
        log.debug("Saving File name : {}", dto.getStoreFileName());

        Mockito.verify(bucket, Mockito.times(1)).create(dto.getStorePath(), imageContent, dto.getExtension());
    }

    @Test
    @DisplayName("파일 저장 실패")
    void storeFileIsNullFailed() {
        Mockito.when(multipartFile.isEmpty()).thenReturn(true);
        Assertions.assertThrows(CustomException.class, () -> firestore.storeFile("testDirectory/", multipartFile));
    }

    @Test
    @DisplayName("파일 삭제 성공")
    void deleteFile() {
        String fileName = "test.jpg";
        String filePath = "testDirectory/" + fileName;

        Mockito.when(bucket.get(filePath)).thenReturn(blob);
        Mockito.when(blob.exists()).thenReturn(true);

        Assertions.assertNotEquals(null, blob);
        firestore.deleteFile(filePath);

        Mockito.verify(blob, Mockito.times(1)).delete();
    }

    @Test
    @DisplayName("파일 삭제 실패")
    void deleteFileFailed() {
        String fileName = "test.jpg";
        String filePath = "testDirectory/" + fileName;

        Mockito.when(bucket.get(filePath)).thenReturn(null);
        Assertions.assertThrows(CustomException.class, () -> firestore.deleteFile(filePath));
    }
}
