package com.project.forde.batch.csv.user.info;

import com.project.forde.batch.csv.CSVWriterHelper;
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
public class UserCSVWriter implements ItemWriter<UserDto> {
    private final CSVWriterHelper<UserDto> csvLogWriterHelper;

    @BeforeStep
    public void beforeStep() {
        csvLogWriterHelper.resetFirstWrite();
    }

    public void write(Chunk<? extends UserDto> chunk) throws Exception {
        String[] fieldNames = {"userId", "nickname", "description", "boardCount", "newsCount", "commentCount", "likeCount", "followerCount", "followingCount"};

        csvLogWriterHelper.write(
                chunk,
                "user_log",
                fieldNames
        );
    }
}
