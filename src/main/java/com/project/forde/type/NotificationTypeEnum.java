package com.project.forde.type;

import lombok.Getter;

@Getter
public enum NotificationTypeEnum {
    CONNECTED("1000"),
    NOTICE("1001"),
    BOARD_COMMENT("1002"),
    MENTION("1003"),
    BOARD_LIKE("1004"),
    RECOMMEND("1005"),
    FOLLOWING_POST("1006"),
    EVENT("1007"),
    FOLLOW("1008"),
    FOLLOWING("1009"),
    ;

    private final String type;

    NotificationTypeEnum(String type) {
        this.type = type;
    }
}
