package com.project.forde.type;

import lombok.Getter;


@Getter
public enum ImageActionEnum {
    UPLOAD("UPLOAD"),
    KEEP("KEEP"),
    DELETE("DELETE");

    private final String type;

    ImageActionEnum(String type) {
        this.type = type;
    }
}
