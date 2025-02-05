package com.project.forde.batch.csv.comment;

import com.project.forde.batch.csv.CSVLogDto;
import com.project.forde.batch.csv.LogType;
import com.project.forde.entity.BoardLike;
import com.project.forde.entity.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentLogCsvProcessor implements ItemProcessor<Comment, CSVLogDto> {
    @Override
    public CSVLogDto process(Comment item) {
        CSVLogDto csvLogDto = new CSVLogDto(
                item.getUploader().getUserId(),
                item.getBoard().getBoardId(),
                LogType.COMMENT,
                null,
                null,
                item.getCreatedTime().toString()
        );

        log.info("csvLogDto: {}", csvLogDto);
        return csvLogDto;
    }
}
