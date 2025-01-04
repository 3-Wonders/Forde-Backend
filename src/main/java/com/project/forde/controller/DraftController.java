package com.project.forde.controller;

import com.project.forde.dto.draft.DraftDto;
import com.project.forde.service.DraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/draft")
public class DraftController {
    private final DraftService draftService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDraft(@Valid final DraftDto.Request request) {
        draftService.create(1L, request);
        return ResponseEntity.noContent().build();
    }
}
