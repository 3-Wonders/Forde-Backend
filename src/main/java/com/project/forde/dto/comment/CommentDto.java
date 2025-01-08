package com.project.forde.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.dto.mention.MentionDto;
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

    public static class Response {
        @Getter
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Comment {
            private Long commentId;
            private Long parentId;
            private AppUserDto.Response.Intro uploader;
            private List<MentionDto.Response.Mention> mentions;
            private String content;
            private Boolean hasReply;
            private Boolean isAdopt;
            private String createdTime;
        }

        @Getter
        @AllArgsConstructor
        public static class Comments {
            private List<Comment> comments;
            private Long total;
        }
    }
}
