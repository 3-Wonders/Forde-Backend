package com.project.forde.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestMailCompareDto {
    private String email;
    private String verifyCode;
}
