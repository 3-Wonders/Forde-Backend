package com.project.forde.controller;

import com.project.forde.dto.notification.NotificationDto;
import com.project.forde.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/notification")
@ResponseBody
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(path = "/emitter", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter emitter(@RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {
        return notificationService.subscribe(lastEventId);
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "count", defaultValue = "5") int count
    ) {
        return ResponseEntity.ok(notificationService.getNotifications(page, count));
    }

    @GetMapping("/following/count")
    public ResponseEntity<?> getFollowingPostsCount() {
        return ResponseEntity.ok(notificationService.getFollowingPostsCount());
    }

    @GetMapping("/following/new")
    public ResponseEntity<?> getFollowingNewPosts() {
        return ResponseEntity.ok(notificationService.hasNewFollowingPost());
    }

    @PostMapping
    public ResponseEntity<?> readNotification(@Valid @RequestBody NotificationDto.Request.ReadNotification request) {
        notificationService.readNotification(request.getNotificationIds());
        return ResponseEntity.noContent().build();
    }
}
