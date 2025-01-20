package com.project.forde.controller;

import com.project.forde.dto.board.BoardDto;
import com.project.forde.service.BoardService;
import com.project.forde.type.BoardTypeEnum;
import com.project.forde.type.SortBoardTypeEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@ResponseBody
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/recent")
    public ResponseEntity<?> getPosts(
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        return ResponseEntity.ok(boardService.getRecentPosts(page, count, SortBoardTypeEnum.ALL));
    }

    @GetMapping("/news")
    public ResponseEntity<?> getNews(
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        return ResponseEntity.ok(boardService.getRecentPosts(page, count, SortBoardTypeEnum.N));
    }

    @GetMapping("/board")
    public ResponseEntity<?> getBoards(
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        return ResponseEntity.ok(boardService.getRecentPosts(page, count, SortBoardTypeEnum.B));
    }

    @GetMapping("/question")
    public ResponseEntity<?> getQuestions(
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        return ResponseEntity.ok(boardService.getRecentPosts(page, count, SortBoardTypeEnum.Q));
    }

    @GetMapping("/board/search")
    public ResponseEntity<?> getPostSearch(
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count,
            @RequestParam(value = "keyword") final String keyword
    ) {
        return ResponseEntity.ok(boardService.getSearchPosts(page, count, keyword));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> getPost(@PathVariable("boardId") final Long boardId) {
        return ResponseEntity.ok(boardService.getPost(boardId));
    }

    @GetMapping("/board/{boardId}/update")
    public ResponseEntity<?> getUpdatePost(@PathVariable("boardId") final Long boardId) {
        return ResponseEntity.ok(boardService.getUpdatePost(1L, boardId));
    }

    @GetMapping("/board/following")
    public ResponseEntity<?> getFollowingNews(
            @RequestParam(value = "type", required = false, defaultValue = "A") Character type,
            @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
            @RequestParam(value = "count", required = false, defaultValue = "5") final int count
    ) {
        boolean isType = BoardTypeEnum.findByType(type) != null;

        if (!isType) {
            type = null;
        }

        return ResponseEntity.ok(boardService.getFollowingNews(page, count, type));
    }

    @PostMapping(value = "/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBoard(@Valid @ModelAttribute final BoardDto.Request.Create request) {
        Long boardId = boardService.create(29L, request);
        return ResponseEntity.created(URI.create("/" + boardId)).build();
    }

    @PatchMapping(value = "/board/{boardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBoard(@PathVariable("boardId") final Long boardId, @Valid @ModelAttribute final BoardDto.Request.Update request) {
        boardService.update(1L, boardId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable("boardId") final Long boardId) {
        boardService.delete(1L, boardId);
        return ResponseEntity.noContent().build();
    }
}
