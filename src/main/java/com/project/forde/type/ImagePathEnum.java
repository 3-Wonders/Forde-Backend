package com.project.forde.type;

import lombok.Getter;

@Getter
public enum ImagePathEnum {
    BOARD("board/");

    private final String path;

    ImagePathEnum(String path) { this.path = path; }
}
