package com.project.forde.batch.csv;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class CSVLogWriterHelper {
    public void write(Chunk<? extends CSVLogDto> chunk, String filename) throws Exception {
        BeanWrapperFieldExtractor<CSVLogDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"userId", "boardId", "logType", "duration", "revisit", "date"});

        DelimitedLineAggregator<CSVLogDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<CSVLogDto> itemWriter = new FlatFileItemWriterBuilder<CSVLogDto>()
                .name("csvLogWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource("output/" + filename + ".csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("userId,boardId,logType,duration,revisit,date"))
                .build();

        itemWriter.afterPropertiesSet();

        itemWriter.open(new ExecutionContext());
        itemWriter.write(chunk);

        itemWriter.close();
    }
}
