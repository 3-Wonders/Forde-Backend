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

    @PostMapping
    public ResponseEntity<?> create(@PathVariable("board_id") final Long boardId, @Valid @RequestBody final CommentDto.Request request) {
        commentService.create(1L, boardId, request);
        return ResponseEntity.created(URI.create("/board/" + boardId + "/comment")).build();
    }
}
