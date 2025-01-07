package com.project.forde.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class CommentDto {
    @Getter
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;
        private List<Long> userId;
    }
}
