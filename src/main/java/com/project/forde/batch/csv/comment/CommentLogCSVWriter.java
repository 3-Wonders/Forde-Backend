package com.project.forde.batch.csv.comment;

import com.project.forde.batch.csv.CSVLogDto;
import com.project.forde.batch.csv.CSVLogWriterHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentLogCSVWriter implements ItemWriter<CSVLogDto> {
    private final CSVLogWriterHelper csvLogWriterHelper;

    @Override
    public void write(Chunk<? extends CSVLogDto> chunk) throws Exception {
        csvLogWriterHelper.write(chunk, "comment_log");
    }
}
