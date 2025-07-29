package com.project.forde.batch.csv.log.comment;

import com.project.forde.entity.Comment;
import com.project.forde.util.CustomTimestamp;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentLogCSVReader implements ItemReader<Comment> {
    private final EntityManagerFactory entityManagerFactory;
    private JpaPagingItemReader<Comment> commentReader;

    @PostConstruct
    public void init() {
        log.info("LikeLogCSVReader.init() 호출");
        commentReader = createCommentReader();
        this.commentReader.open(new ExecutionContext());
    }

    @Override
    public Comment read() throws Exception {
        return commentReader.read();
    }

    private JpaPagingItemReader<Comment> createCommentReader() {
        LocalDateTime interval = new CustomTimestamp().getTimestamp().minusMonths(3);

        return new JpaPagingItemReaderBuilder<Comment>()
                .name("recommendCommentReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT c
                        FROM Comment c
                        WHERE c.createdTime >= :interval
                        """)
                .parameterValues(Map.of("interval", interval))
                .pageSize(100)
                .build();
    }
}
