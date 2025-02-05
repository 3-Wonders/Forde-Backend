package com.project.forde.batch.csv.like;

import com.project.forde.entity.BoardLike;
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
public class LikeLogCSVReader implements ItemReader<BoardLike> {
    private final EntityManagerFactory entityManagerFactory;
    private JpaPagingItemReader<BoardLike> likeReader;

    @PostConstruct
    public void init() {
        log.info("LikeLogCSVReader.init() 호출");
        likeReader = createLikeReader();
        this.likeReader.open(new ExecutionContext());
    }

    @Override
    public BoardLike read() throws Exception {
        return likeReader.read();
    }

    private JpaPagingItemReader<BoardLike> createLikeReader() {
        LocalDateTime interval = new CustomTimestamp().getTimestamp().minusMonths(3);

        return new JpaPagingItemReaderBuilder<BoardLike>()
                .name("recommendLikeReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT bk
                        FROM BoardLike bk
                        WHERE bk.createdTime >= :interval
                        """)
                .parameterValues(Map.of("interval", interval))
                .pageSize(100)
                .build();
    }
}
