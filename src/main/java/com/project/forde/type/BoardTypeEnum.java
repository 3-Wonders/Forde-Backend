package com.project.forde.type;

import lombok.Getter;

@Getter
public enum BoardTypeEnum {
    N('N'),
    B('B'),
    Q('Q');

    private final Character type;

    BoardTypeEnum(Character type) {
        this.type = type;
    }

    public static Character findByType(Character type) {
        for (BoardTypeEnum boardTypeEnum : values()) {
            if (boardTypeEnum.getType().toString().equals(type.toString().toUpperCase())) {
                return boardTypeEnum.getType();
            }
        }

        return null;
    }
}
