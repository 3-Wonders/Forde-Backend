package com.project.forde.dto.dummyImage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class DummyImageDto {

    public static class Response {
        @Getter
        @Setter
        @AllArgsConstructor
        public static class Image {
            private final Long imageId;
            private final String path;
        }
    }
}
