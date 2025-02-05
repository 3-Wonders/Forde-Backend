package com.project.forde.batch.csv.like;

import com.project.forde.batch.csv.CSVLogDto;
import com.project.forde.batch.csv.LogType;
import com.project.forde.entity.BoardLike;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikeLogCsvProcessor implements ItemProcessor<BoardLike, CSVLogDto> {
    @Override
    public CSVLogDto process(BoardLike item) {
        CSVLogDto csvLogDto = new CSVLogDto(
                item.getBoardLikePK().getUser().getUserId(),
                item.getBoardLikePK().getBoard().getBoardId(),
                LogType.LIKE,
                null,
                null,
                item.getCreatedTime().toString()
        );

        log.info("csvLogDto: {}", csvLogDto);
        return csvLogDto;
    }
}
