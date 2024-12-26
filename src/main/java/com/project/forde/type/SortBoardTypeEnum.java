package com.project.forde.type;

import lombok.Getter;

@Getter
public enum SortBoardTypeEnum {
    ALL('A'),
    N('N'),
    B('B');

    private final Character type;

    SortBoardTypeEnum(Character type) {
        this.type = type;
    }
}
