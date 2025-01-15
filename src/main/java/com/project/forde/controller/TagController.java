package com.project.forde.controller;

import com.project.forde.dto.tag.TagDto;
import com.project.forde.service.BoardService;
import com.project.forde.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tag")
@ResponseBody
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<?> getTags(
            @RequestParam final String keyword
    ) {
        return ResponseEntity.ok(tagService.getTags(keyword));
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPostsWithTag(
            @RequestParam final String keyword,
            @RequestParam(value = "page", defaultValue = "1") final int page,
            @RequestParam(value = "count", defaultValue = "10") final int count
    ) {
        return ResponseEntity.ok(boardService.getPostsWithTag(keyword, page, count));
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularTags(
            @RequestParam(value = "count", defaultValue = "6") final int count
    ) {
        return ResponseEntity.ok(tagService.getPopularTags(count));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody final TagDto.Request request) {
        return ResponseEntity.ok(tagService.create(request));
    }
}
