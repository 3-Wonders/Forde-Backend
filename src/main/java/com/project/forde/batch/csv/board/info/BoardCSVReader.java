package com.project.forde.batch.csv.board.info;

import com.project.forde.entity.Board;
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
public class BoardCSVReader implements ItemReader<Board> {
    private final EntityManagerFactory entityManagerFactory;
    private JpaPagingItemReader<Board> boardReader;

    @PostConstruct
    public void init() {
        log.info("BoardCSVReader.init() 호출");
        boardReader = createBoardReader();
        this.boardReader.open(new ExecutionContext());
    }

    @Override
    public Board read() throws Exception {
        return boardReader.read();
    }

    private JpaPagingItemReader<Board> createBoardReader() {
        LocalDateTime interval = new CustomTimestamp().getTimestamp().minusMonths(3);

        return new JpaPagingItemReaderBuilder<Board>()
                .name("boardReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT b FROM Board b
                        WHERE b.createdTime >= :interval
                        """)
                .parameterValues(Map.of("interval", interval))
                .pageSize(100)
                .build();
    }
}
