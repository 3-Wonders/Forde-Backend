package com.project.forde.batch.csv.board.dto;

import lombok.Builder;

@Builder
public record BoardDto(
        Long userId,
        Long boardId,
        Character boardType,
        String title,
        Integer likeCount,
        Integer commentCount,
        Integer viewCount,
        String createdTime
) {
}
