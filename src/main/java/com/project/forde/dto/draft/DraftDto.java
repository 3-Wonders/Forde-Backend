package com.project.forde.dto.draft;

import com.project.forde.type.BoardTypeEnum;
import com.project.forde.validation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
        private final List<Long> imageIds;
    }
}
