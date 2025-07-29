package com.project.forde.controller;

import com.project.forde.service.SnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@ResponseBody
public class SnsController {
    private final SnsService snsService;

}
