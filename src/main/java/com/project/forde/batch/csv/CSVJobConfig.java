package com.project.forde.batch.csv;

import com.project.forde.batch.csv.activitylog.ActivityLogCSVReader;
import com.project.forde.batch.csv.activitylog.ActivityLogCSVWriter;
import com.project.forde.batch.csv.activitylog.ActivityLogCsvProcessor;
import com.project.forde.batch.csv.comment.CommentLogCSVReader;
import com.project.forde.batch.csv.comment.CommentLogCSVWriter;
import com.project.forde.batch.csv.comment.CommentLogCsvProcessor;
import com.project.forde.batch.csv.like.LikeLogCSVReader;
import com.project.forde.batch.csv.like.LikeLogCSVWriter;
import com.project.forde.batch.csv.like.LikeLogCsvProcessor;
import com.project.forde.batch.csv.view.ViewLogCSVReader;
import com.project.forde.batch.csv.view.ViewLogCSVWriter;
import com.project.forde.batch.csv.view.ViewLogCsvProcessor;
import com.project.forde.entity.ActivityLog;
import com.project.forde.entity.BoardLike;
import com.project.forde.entity.BoardView;
import com.project.forde.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CSVJobConfig {
    private final ActivityLogCSVReader activityLogCSVReader;
    private final ActivityLogCsvProcessor activityLogCsvProcessor;
    private final ActivityLogCSVWriter activityLogCSVWriter;

    private final LikeLogCSVReader likeLogCSVReader;
    private final LikeLogCsvProcessor likeLogCsvProcessor;
    private final LikeLogCSVWriter likeLogCSVWriter;

    private final CommentLogCSVReader commentLogCSVReader;
    private final CommentLogCsvProcessor commentLogCsvProcessor;
    private final CommentLogCSVWriter commentLogCSVWriter;

    private final ViewLogCSVReader viewLogCSVReader;
    private final ViewLogCSVWriter viewLogCSVWriter;
    private final ViewLogCsvProcessor viewLogCsvProcessor;

    @Bean
    public Job csvJob(JobRepository jobRepository, Step csvUserStep, Step csvLikeStep, Step csvCommentStep, Step csvViewStep) {
        log.info("csvJob 호출");

        return new JobBuilder("csvJob", jobRepository)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(csvUserStep)
                .next(csvLikeStep)
                .next(csvCommentStep)
                .next(csvViewStep)
                .build();
    }

    @Bean
    public Step csvUserStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("csvStep 호출");

        return new StepBuilder("csvUserStep", jobRepository)
                .<ActivityLog, CSVLogDto>chunk(100, transactionManager)
                .reader(activityLogCSVReader)
                .processor(activityLogCsvProcessor)
                .writer(activityLogCSVWriter)
                .build();
    }

    @Bean
    public Step csvLikeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("csvLikeStep 호출");

        return new StepBuilder("csvLikeStep", jobRepository)
                .<BoardLike, CSVLogDto>chunk(100, transactionManager)
                .reader(likeLogCSVReader)
                .processor(likeLogCsvProcessor)
                .writer(likeLogCSVWriter)
                .build();
    }

    @Bean
    public Step csvCommentStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("csvCommentStep 호출");

        return new StepBuilder("csvCommentStep", jobRepository)
                .<Comment, CSVLogDto>chunk(100, transactionManager)
                .reader(commentLogCSVReader)
                .processor(commentLogCsvProcessor)
                .writer(commentLogCSVWriter)
                .build();
    }

    @Bean
    public Step csvViewStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("csvViewStep 호출");

        return new StepBuilder("csvViewStep", jobRepository)
                .<BoardView, CSVLogDto>chunk(100, transactionManager)
                .reader(viewLogCSVReader)
                .processor(viewLogCsvProcessor)
                .writer(viewLogCSVWriter)
                .build();
    }
}
