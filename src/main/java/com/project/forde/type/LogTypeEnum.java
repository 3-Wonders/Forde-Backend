package com.project.forde.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LogTypeEnum {
    DURATION("duration"),
    SEARCH("search"),
    REVISIT("revisit"),
    ;

    private final String logType;
}
