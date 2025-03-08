package com.project.forde.controller;

import com.project.forde.dto.activityLog.ActivityLogDto;
import com.project.forde.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
@ResponseBody
public class ActivityLogController {
    private final ActivityLogService activityLogService;

    @PostMapping("/duration")
    public ResponseEntity<?> publishDuration(@Valid @RequestBody ActivityLogDto.Request.Create request) {
        activityLogService.publishDuration(request);
        return ResponseEntity.ok().build();
    }
}
