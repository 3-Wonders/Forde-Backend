package com.project.forde.controller;

import com.project.forde.service.DummyImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@ResponseBody
public class DummyImageController {
    private final DummyImageService dummyImageService;

    @PostMapping(value = "/dummy/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("image") final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(dummyImageService.createImage(image));
    }
}
