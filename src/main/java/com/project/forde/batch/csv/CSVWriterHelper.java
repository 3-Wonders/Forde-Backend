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
public class CSVWriterHelper<T> {
    private boolean isFirstWrite = true;

    public void write(Chunk<? extends T> chunk, String fileName, String[] fieldNames) throws Exception {
        BeanWrapperFieldExtractor<T> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(fieldNames);

        DelimitedLineAggregator<T> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<T> itemWriter = new FlatFileItemWriterBuilder<T>()
                .name(fileName + "Writer")
                .encoding("UTF-8")
                .resource(new FileSystemResource("output/" + fileName + ".csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write(String.join(",", fieldNames)))
                .append(!isFirstWrite)
                .build();

        itemWriter.afterPropertiesSet();

        itemWriter.open(new ExecutionContext());
        itemWriter.write(chunk);

        itemWriter.close();

        if (isFirstWrite) {
            isFirstWrite = false;
        }
    }

    public void resetFirstWrite() {
        isFirstWrite = true;
    }
}
