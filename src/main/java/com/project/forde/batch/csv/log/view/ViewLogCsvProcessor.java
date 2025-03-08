package com.project.forde.batch.csv.log.view;

import com.project.forde.batch.csv.log.LogType;
import com.project.forde.batch.csv.log.dto.CSVLogDto;
import com.project.forde.entity.BoardView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViewLogCsvProcessor implements ItemProcessor<BoardView, CSVLogDto> {
    @Override
    public CSVLogDto process(BoardView item) {
        CSVLogDto csvLogDto = new CSVLogDto(
                item.getBoardViewPK().getUser().getUserId(),
                item.getBoardViewPK().getBoard().getBoardId(),
                LogType.VIEW,
                null,
                null,
                item.getCreatedTime().toString()
        );

        log.info("csvLogDto: {}", csvLogDto);
        return csvLogDto;
    }
}
