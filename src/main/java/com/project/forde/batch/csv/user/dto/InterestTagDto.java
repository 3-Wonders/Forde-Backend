package com.project.forde.batch.csv.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class InterestTagDto{
    private Long userId;
    private String tagName;
    private LocalDateTime createdTime;
}
