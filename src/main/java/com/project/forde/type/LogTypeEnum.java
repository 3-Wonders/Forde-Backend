package com.project.forde.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogTypeEnum {
    DURATION("duration"),
    SEARCH("search"),
    REVISIT("revisit"),
    ;

    private final String logType;
}