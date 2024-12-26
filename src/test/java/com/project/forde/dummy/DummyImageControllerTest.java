package com.project.forde.dummy;

import com.project.forde.controller.DummyImageController;
import com.project.forde.service.DummyImageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(DummyImageController.class)
@AutoConfigureMockMvc
public class DummyImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DummyImageService dummyImageService;

    @Test
    @DisplayName("더미 이미지 추가")
    void createDummyImage() throws Exception {
        mockMvc.perform(multipart("/dummy/image").file("image", "test".getBytes()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("더미 이미지 추가 실패")
    void createDummyImageFail() throws Exception {
        mockMvc.perform(multipart("/dummy/image"))
            .andExpect(status().isBadRequest());
    }
}
