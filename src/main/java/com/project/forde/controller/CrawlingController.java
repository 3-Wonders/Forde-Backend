package com.project.forde.controller;

import com.project.forde.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crawl")
@ResponseBody
public class CrawlingController {
    private final CrawlingService crawlingService;

    @PostMapping(value = "")
    public ResponseEntity<?> getDevsNote() {
        crawlingService.getCrawling();
        return ResponseEntity.noContent().build();
    }
}
