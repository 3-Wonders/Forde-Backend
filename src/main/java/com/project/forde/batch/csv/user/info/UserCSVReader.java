package com.project.forde.batch.csv.user.info;

import com.project.forde.entity.AppUser;
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
public class UserCSVReader implements ItemReader<AppUser> {
    private final EntityManagerFactory entityManagerFactory;
    private JpaPagingItemReader<AppUser> userReader;

    @PostConstruct
    public void init() {
        log.info("UserCSVReader.init() 호출");
        userReader = createUserReader();
        this.userReader.open(new ExecutionContext());
    }

    @Override
    public AppUser read() throws Exception {
        return userReader.read();
    }

    private JpaPagingItemReader<AppUser> createUserReader() {
        LocalDateTime interval = new CustomTimestamp().getTimestamp().minusMonths(3);

        return new JpaPagingItemReaderBuilder<AppUser>()
                .name("userReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT u FROM AppUser u
                        JOIN LoginLog ll ON u = ll.user
                        WHERE ll.loggedInTime >= :interval
                        """)
                .parameterValues(Map.of("interval", interval))
                .pageSize(100)
                .build();
    }
}
