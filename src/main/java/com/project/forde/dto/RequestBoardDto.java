package com.project.forde.dto;

import com.project.forde.type.BoardTypeEnum;
import com.project.forde.validation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class RequestBoardDto {
    @EnumValue(enumClass = BoardTypeEnum.class, message = "게시판 유형은 뉴스 또는 게시판이어야 합니다.", ignoreCase = true)
    private String boardType;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 20, message = "제목은 20자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "태그를 1 ~ 3개 선택해주세요.")
    @Size(min = 1, max = 3, message = "태그를 1 ~ 3개 선택해주세요.")
    private List<Long> tagIds;

    private MultipartFile thumbnail;
    private List<Long> imageIds;
}
