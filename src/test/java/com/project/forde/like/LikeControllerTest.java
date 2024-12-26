package com.project.forde.like;

import com.project.forde.controller.LikeController;
import com.project.forde.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(LikeController.class)
@AutoConfigureMockMvc
public class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @Test
    @DisplayName("좋아요 추가")
    void createLike() throws Exception {
        Long boardId = 1L;

        mockMvc.perform(post("/board/" + boardId + "/like"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("좋아요 추가 - 실패")
    void createLikeFail() throws Exception {
        String boardId = "Invalid";

        mockMvc.perform(post("/board/" + boardId + "/like"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("좋아요 삭제")
    void deleteLike() throws Exception {
        Long boardId = 1L;

        mockMvc.perform(post("/board/" + boardId + "/like"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("좋아요 삭제 - 실패")
    void deleteLikeFail() throws Exception {
        String boardId = "Invalid";

        mockMvc.perform(post("/board/" + boardId + "/like"))
            .andExpect(status().isBadRequest());
    }
}
