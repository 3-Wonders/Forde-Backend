package com.project.forde.batch.csv.board.info;

import com.project.forde.batch.csv.CSVWriterHelper;
import com.project.forde.batch.csv.board.dto.BoardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardCSVWriter implements ItemWriter<BoardDto> {
    private final CSVWriterHelper<BoardDto> csvLogWriterHelper;

    @BeforeStep
    public void beforeStep() {
        csvLogWriterHelper.resetFirstWrite();
    }

    @Override
    public void write(Chunk<? extends BoardDto> chunk) throws Exception {
        String[] fieldNames = {"userId", "boardId", "boardType", "title", "commentCount", "likeCount", "viewCount", "createdTime"};

        csvLogWriterHelper.write(
                chunk,
                "board_log",
                fieldNames
        );
    }
}
