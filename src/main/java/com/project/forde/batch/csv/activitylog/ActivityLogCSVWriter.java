package com.project.forde.batch.csv.activitylog;

import com.project.forde.batch.csv.CSVLogDto;
import com.project.forde.batch.csv.CSVLogWriterHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityLogCSVWriter implements ItemWriter<CSVLogDto> {
    private final CSVLogWriterHelper csvLogWriterHelper;

    @Override
    public void write(Chunk<? extends CSVLogDto> chunk) throws Exception {
        csvLogWriterHelper.write(chunk, "activity_log");
    }
}
