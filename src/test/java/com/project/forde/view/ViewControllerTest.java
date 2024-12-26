package com.project.forde.view;

import com.project.forde.controller.ViewController;
import com.project.forde.service.ViewService;
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
@WebMvcTest(ViewController.class)
@AutoConfigureMockMvc
public class ViewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ViewService viewService;

    @Test
    @DisplayName("게시글 조회수 증가")
    void increaseViewCount() throws Exception {
        Long boardId = 1L;

        mockMvc.perform(post("/board/" + boardId + "/view"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("게시글 조회수 증가 실패")
    void increaseViewCountFail() throws Exception {
        String boardId = "InvalidId";

        mockMvc.perform(post("/board/" + boardId + "/view"))
            .andExpect(status().isBadRequest());
    }
}
