package com.project.forde.dto.draft;

import com.project.forde.dto.tag.TagDto;
import com.project.forde.type.BoardTypeEnum;
import com.project.forde.type.ImageActionEnum;
import com.project.forde.validation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class DraftDto {
    @Getter
    @AllArgsConstructor
    public static class Request {
        @EnumValue(enumClass = BoardTypeEnum.class, message = "게시판 유형은 뉴스, 게시판, 질문이어야 합니다.", ignoreCase = true)
        private final String boardType;
        private final String title;
        private final String content;
        private final List<Long> tagIds;
        private final MultipartFile thumbnail;
        @EnumValue(enumClass = ImageActionEnum.class, message = "UPLOAD, KEEP, DELETE만 선택 가능합니다.", ignoreCase = true)
        private final String thumbnailAction;
        private final List<Long> imageIds;

        public String getThumbnailAction() {
            return thumbnailAction.toUpperCase();
        }
    }

    public static class Response {

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Draft {
            private Long draftId;
            private Character boardType;
            private String title;
            private String content;
            private String thumbnail;
            private List<TagDto.Response.Tag> tags;
            private List<Long> imageIds;
            private String createdTime;
        }
    }
}
