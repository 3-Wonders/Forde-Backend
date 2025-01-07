package com.project.forde.controller;

import com.project.forde.dto.draft.DraftDto;
import com.project.forde.service.DraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/draft")
public class DraftController {
    private final DraftService draftService;

    @GetMapping()
    public ResponseEntity<?> getDrafts() {
        return ResponseEntity.ok(draftService.getDrafts(1L));
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDraft(@Valid final DraftDto.Request request) {
        draftService.create(1L, request);
        return ResponseEntity.created(URI.create("/draft")).build();
    }

    @PatchMapping(value = "/{draftId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDraft(@PathVariable final Long draftId, @Valid final DraftDto.Request request) {
        draftService.update(1L, draftId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{draftId}")
    public ResponseEntity<?> deleteDraft(@PathVariable final Long draftId) {
        draftService.delete(1L, draftId);
        return ResponseEntity.noContent().build();
    }
}
