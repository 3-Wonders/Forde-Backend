package com.project.forde.batch.csv.board.info;

import com.project.forde.batch.csv.board.dto.BoardDto;
import com.project.forde.entity.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BoardCsvProcessor implements ItemProcessor<Board, BoardDto> {
    @Override
    public BoardDto process(Board item) {
        BoardDto boardDto = BoardDto.builder()
                .userId(item.getUploader().getUserId())
                .boardId(item.getBoardId())
                .boardType(item.getCategory())
                .title(item.getTitle())
                .commentCount(item.getCommentCount())
                .likeCount(item.getLikeCount())
                .viewCount(item.getViewCount())
                .createdTime(item.getCreatedTime().toString())
                .build();

        log.info("boardDto: {}", boardDto);
        return boardDto;
    }
}
