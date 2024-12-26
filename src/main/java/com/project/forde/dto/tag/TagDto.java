package com.project.forde.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class TagDto {
    public static class Response {

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
