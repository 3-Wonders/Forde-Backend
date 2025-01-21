package com.project.forde.controller;

import com.project.forde.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@ResponseBody
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/board/{boardId}/like")
    public ResponseEntity<?> createLike(
            @PathVariable("boardId") final Long boardId
    ) {
        likeService.createLike(boardId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/board/{boardId}/like")
    public ResponseEntity<?> deleteLike(
            @PathVariable("boardId") final Long boardId
    ) {
        likeService.deleteLike(boardId);
        return ResponseEntity.noContent().build();
    }
}
