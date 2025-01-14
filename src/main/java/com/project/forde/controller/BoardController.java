package com.project.forde.controller;

import com.project.forde.dto.board.BoardDto;
import com.project.forde.service.BoardService;
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

    @PostMapping(value = "/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBoard(@Valid @ModelAttribute final BoardDto.Request request) {
        Long boardId = boardService.create(1L, request);
        return ResponseEntity.created(URI.create("/" + boardId)).build();
    }
}
