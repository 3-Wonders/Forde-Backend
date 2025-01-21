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
            @RequestParam(value = "count", defaultValue = "5") final int count
    ) {
        return ResponseEntity.ok(commentService.getComments(boardId, page, count));
    }

    @GetMapping("/{parent_id}")
    public ResponseEntity<?> getReplies(
            @PathVariable("board_id") final Long boardId,
            @PathVariable("parent_id") final Long parentId,
            @RequestParam(value = "page", defaultValue = "1") final int page,
            @RequestParam(value = "count", defaultValue = "5") final int count
    ) {
        return ResponseEntity.ok(commentService.getReplies(boardId, parentId, page, count));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("board_id") final Long boardId,
            @Valid @RequestBody final CommentDto.Request request
    ) {
        commentService.create(boardId, request);
        return ResponseEntity.created(URI.create("/board/" + boardId + "/comment")).build();
    }

    @PostMapping("/{parent_id}")
    public ResponseEntity<?> createReply(
            @PathVariable("board_id") final Long boardId,
            @PathVariable("parent_id") final Long parentId,
            @Valid @RequestBody final CommentDto.Request request
    ) {
        commentService.createReply(boardId, parentId, request);
        return ResponseEntity.created(URI.create("/board/" + boardId + "/comment/" + parentId)).build();
    }

    @PostMapping("/{comment_id}/adopt")
    public ResponseEntity<?> adopt(
            @PathVariable("board_id") final Long boardId,
            @PathVariable("comment_id") final Long commentId
    ) {
        commentService.adopt(boardId, commentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<?> update(
            @PathVariable("board_id") final Long boardId,
            @PathVariable("comment_id") final Long commentId,
            @Valid @RequestBody final CommentDto.Request request
    ) {
        commentService.update(boardId, commentId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<?> delete(
            @PathVariable("board_id") final Long boardId,
            @PathVariable("comment_id") final Long commentId
    ) {
        commentService.delete(boardId, commentId);
        return ResponseEntity.noContent().build();
    }
}
