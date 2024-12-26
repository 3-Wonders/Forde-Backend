package com.project.forde.board;

import com.project.forde.controller.BoardController;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.dto.board.BoardDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc
public class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BoardService boardService;

    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01};
        mockMultipartFile = new MockMultipartFile("thumbnail", "test.jpg", "image/jpeg", imageContent);
    }

    static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.arguments("N", null, "content", List.of(1L), 400), // 제목 없음
                Arguments.arguments("N", "title", null, List.of(1L), 400), // 내용 없음
                Arguments.arguments("N", "title", "content", List.of(1L, 2L, 3L, 4L), 400), // 태그 개수 초과
                Arguments.arguments("N", "title", "content", null, 400), // 태그 없음
                Arguments.arguments("C", "title", "content", List.of(1L, 2L, 3L), 400) // 게시글 유형이 없음
        );
    }

    @DisplayName("게시글 생성 실패")
    @ParameterizedTest
    @MethodSource("generateData")
    void createBoardFailed(String boardType, String title, String content, List<Long> tagIds, int expectedCode) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = multipart(HttpMethod.POST, "/board")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("boardType", boardType)
                .param("title", title)
                .param("content", content);

        if (tagIds != null) {
            for (Long tagId : tagIds) {
                requestBuilder.param("tagIds", tagId.toString());
            }
        }

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedCode));

        log.debug("Request Payload: {}", boardType + " " + title + " " + content + " " + tagIds);
    }

    @Test
    @DisplayName("게시글 생성 성공")
    @Rollback
    void createBoardSuccess() throws Exception {
        mockMvc.perform(
            multipart(HttpMethod.POST, "/board")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("boardType", "N")
                .param("title", "title2")
                .param("content", "content2")
                .param("tagIds", "1")
        )
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 생성 및 썸네일 업로드 성공")
    @Rollback
    void createBoardSuccessWithThumbnail() throws Exception {
        mockMvc.perform(
            multipart(HttpMethod.POST, "/board")
                .file("thumbnail", mockMultipartFile.getBytes())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("boardType", "N")
                .param("title", "title3")
                .param("content", "content3")
                .param("tagIds", "1")
        ).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 생성 및 이미지 업로드 성공")
    @Rollback
    void createBoardSuccessWithImage() throws Exception {
        mockMvc.perform(
            multipart(HttpMethod.POST, "/board")
                .file("image", mockMultipartFile.getBytes())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("boardType", "N")
                .param("title", "title4")
                .param("content", "content4")
                .param("tagIds", "1")
                .param("imageIds", "1")
        ).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("모든 게시글 가져오기 성공")
    void getPostsSuccess() throws Exception {
        mockMvc.perform(
                get("/recent")
                    .param("page", "1")
                    .param("count", "10")
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("뉴스 가져오기 성공")
    void getNewsSuccess() throws Exception {
        mockMvc.perform(
                get("/news")
                    .param("page", "1")
                    .param("count", "10")
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("일반 게시글 가져오기 성공")
    void getBoardsSuccess() throws Exception {
        mockMvc.perform(
                get("/board")
                    .param("page", "1")
                    .param("count", "10")
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 검색 성공")
    void getSearchPostsSuccess() throws Exception {
        mockMvc.perform(
                get("/board/search")
                    .param("page", "1")
                    .param("count", "10")
                    .param("keyword", "keyword")
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 수정 가져오기 성공")
    void getUpdatePostSuccess() throws Exception {
        Mockito.when(boardService.getUpdatePost(1L, 1L)).thenReturn(new BoardDto.Response.Update(
                1L,
                "title",
                "content",
                "content",
                null,
                List.of(new TagDto.Response.Tag(1L, "tag")),
                List.of(1L),
                "2021-08-01 13:00:00"
        ));

        mockMvc.perform(
                get("/board/1/update")
        ).andExpect(status().isOk());

        Mockito.verify(boardService, Mockito.times(1)).getUpdatePost(1L, 1L);
    }

    @Test
    @DisplayName("게시글 수정 가져오기 실패 - 유효하지 않은 게시글 ID")
    void getUpdatePostFailedWithInvalidBoardId() throws Exception {
        mockMvc.perform(
                get("/board/invalidBoardId/update")
        ).andExpect(status().isBadRequest());

        Mockito.verify(boardService, Mockito.times(0)).getUpdatePost(1L, 1L);
    }

    @Test
    @DisplayName("게시글 상세 정보 가져오기 성공")
    void getPostSuccess() throws Exception {
        Mockito.when(boardService.getPost(1L)).thenReturn(new BoardDto.Response.Detail(
                1L,
                "title",
                new AppUserDto.Response.Intro(1L, "nickname", "profile"),
                "title",
                "content",
                null,
                List.of(new TagDto.Response.Tag(1L, "tag")),
                false,
                0L,
                0L,
                0L,
                "2021-08-01 13:00:00"
        ));

        mockMvc.perform(
                get("/board/1")
        ).andExpect(status().isOk());

        Mockito.verify(boardService, Mockito.times(1)).getPost(1L);
    }

    @Test
    @DisplayName("게시글 상세 정보 가져오기 실패 - 유효하지 않은 게시글 ID")
    void getPostFailedWithInvalidBoardId() throws Exception {
        mockMvc.perform(
                get("/board/invalidBoardId")
        ).andExpect(status().isBadRequest());

        Mockito.verify(boardService, Mockito.times(0)).getPost(1L);
    }
}