package com.project.forde.batch.csv.user.interest;

import com.project.forde.batch.csv.CSVWriterHelper;
import com.project.forde.batch.csv.user.dto.InterestTagDto;
import com.project.forde.batch.csv.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterestCSVWriter implements ItemWriter<InterestTagDto> {
    private final CSVWriterHelper<InterestTagDto> csvLogWriterHelper;

    @BeforeStep
    public void beforeStep() {
        csvLogWriterHelper.resetFirstWrite();
    }

    public void write(Chunk<? extends InterestTagDto> chunk) throws Exception {
        String[] fieldNames = {"userId", "tagName", "createdTime"};

        csvLogWriterHelper.write(
                chunk,
                "interest_log",
                fieldNames
        );
    }
}
