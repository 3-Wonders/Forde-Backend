package com.project.forde.batch.csv.log.view;

import com.project.forde.entity.BoardLike;
import com.project.forde.entity.BoardView;
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
public class ViewLogCSVReader implements ItemReader<BoardView> {
    private final EntityManagerFactory entityManagerFactory;
    private JpaPagingItemReader<BoardView> viewReader;

    @PostConstruct
    public void init() {
        log.info("LikeLogCSVReader.init() 호출");
        viewReader = createViewReader();
        this.viewReader.open(new ExecutionContext());
    }

    @Override
    public BoardView read() throws Exception {
        return viewReader.read();
    }

    private JpaPagingItemReader<BoardView> createViewReader() {
        LocalDateTime interval = new CustomTimestamp().getTimestamp().minusMonths(3);

        return new JpaPagingItemReaderBuilder<BoardView>()
                .name("recommendViewReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT bv
                        FROM BoardView bv
                        WHERE bv.createdTime >= :interval
                        """)
                .parameterValues(Map.of("interval", interval))
                .pageSize(100)
                .build();
    }
}
