package com.project.forde.controller;

import com.project.forde.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@ResponseBody
public class ViewController {
    private final ViewService viewService;

    @PostMapping("/board/{boardId}/view")
    public ResponseEntity<?> createView(
            @PathVariable("boardId") final Long boardId
    ) {
        viewService.createView(1L, boardId);
        return ResponseEntity.noContent().build();
    }
}
