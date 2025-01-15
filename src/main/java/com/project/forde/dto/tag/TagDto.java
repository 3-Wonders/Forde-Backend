package com.project.forde.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class TagDto {
    @Getter
    public static class Request {
        @NotBlank(message = "태그 이름은 필수입니다.")
        @Size(min = 1, message = "태그 이름은 1자 이상 20자 이하로 입력해주세요.")
        private String tagName;
    }

    public static class Response {

        @Getter
        @Setter
        @AllArgsConstructor
        public static class TagId {
            private Long tagId;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Tag {
            private Long tagId;
            private String tagName;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Tags {
            private List<Tag> tags;
        }
    }
}
