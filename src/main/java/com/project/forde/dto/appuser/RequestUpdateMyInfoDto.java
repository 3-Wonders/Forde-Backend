package com.project.forde.dto.appuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RequestUpdateMyInfoDto {
    @NotBlank(message = "닉네임을 작성해주세요.")
    @Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
    private String nickname;
    private String description;
    private Integer[] interestTags;
}
