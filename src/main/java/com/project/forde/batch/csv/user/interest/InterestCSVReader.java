package com.project.forde.batch.csv.user.interest;

import com.project.forde.batch.csv.user.dto.InterestTagDto;
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
public class InterestCSVReader implements ItemReader<InterestTagDto> {
    private final EntityManagerFactory entityManagerFactory;
    private JpaPagingItemReader<InterestTagDto> interestTag;

    @PostConstruct
    public void init() {
        log.info("InterestCSVReader.init() 호출");
        interestTag = createInterestTagReader();
        this.interestTag.open(new ExecutionContext());
    }

    @Override
    public InterestTagDto read() throws Exception {
        return interestTag.read();
    }

    private JpaPagingItemReader<InterestTagDto> createInterestTagReader() {
        LocalDateTime interval = new CustomTimestamp().getTimestamp().minusMonths(3);

        return new JpaPagingItemReaderBuilder<InterestTagDto>()
                .name("interestTagReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT new com.project.forde.batch.csv.user.dto.InterestTagDto(it.id.appUser.userId, t.tagName, it.createdTime) FROM InterestTag it
                        JOIN Tag t ON it.id.tag = t
                        WHERE it.createdTime >= :interval
                        """)
                .parameterValues(Map.of("interval", interval))
                .pageSize(100)
                .build();
    }
}
