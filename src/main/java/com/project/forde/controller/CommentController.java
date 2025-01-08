package com.project.forde.controller;

import com.project.forde.dto.comment.CommentDto;
import com.project.forde.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/board/{board_id}/comment")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getComments(
            @PathVariable("board_id") final Long boardId,
            @RequestParam(value = "page", defaultValue = "1") final int page,
            @RequestParam(value = "count", defaultValue = "10") final int count
    ) {
        return ResponseEntity.ok(commentService.getComments(boardId, page, count));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("board_id") final Long boardId,
            @Valid @RequestBody final CommentDto.Request request
    ) {
        commentService.create(1L, boardId, request);
        return ResponseEntity.created(URI.create("/board/" + boardId + "/comment")).build();
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<?> update(
            @PathVariable("comment_id") final Long commentId,
            @Valid @RequestBody final CommentDto.Request request
    ) {
        commentService.update(1L, commentId, request);
        return ResponseEntity.noContent().build();
    }
}
