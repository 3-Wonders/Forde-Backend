package com.project.forde.type;

import lombok.Getter;

@Getter
public enum BoardTypeEnum {
    N('N'),
    B('B');

    private final Character type;

    BoardTypeEnum(Character type) {
        this.type = type;
    }
}
