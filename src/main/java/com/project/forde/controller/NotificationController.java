package com.project.forde.controller;

import com.project.forde.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS })
    public SseEmitter emitter(@RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {
        return notificationService.subscribe(lastEventId);
    }
}
