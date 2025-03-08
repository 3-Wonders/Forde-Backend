package com.project.forde.batch.csv.log.view;

import com.project.forde.batch.csv.CSVWriterHelper;
import com.project.forde.batch.csv.log.dto.CSVLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewLogCSVWriter implements ItemWriter<CSVLogDto> {
    private final CSVWriterHelper<CSVLogDto> csvLogWriterHelper;

    @BeforeStep
    public void beforeStep() {
        csvLogWriterHelper.resetFirstWrite();
    }

    @Override
    public void write(Chunk<? extends CSVLogDto> chunk) throws Exception {
        String[] fieldNames = {"userId", "boardId", "logType", "duration", "revisit", "date"};

        csvLogWriterHelper.write(
                chunk,
                "view_log",
                fieldNames
        );
    }
}
