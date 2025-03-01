package com.project.forde.batch.csv;

import com.project.forde.batch.csv.board.dto.BoardDto;
import com.project.forde.batch.csv.board.info.BoardCSVReader;
import com.project.forde.batch.csv.board.info.BoardCSVWriter;
import com.project.forde.batch.csv.board.info.BoardCsvProcessor;
import com.project.forde.batch.csv.log.activitylog.ActivityLogCSVReader;
import com.project.forde.batch.csv.log.activitylog.ActivityLogCSVWriter;
import com.project.forde.batch.csv.log.activitylog.ActivityLogCSVProcessor;
import com.project.forde.batch.csv.log.comment.CommentLogCSVReader;
import com.project.forde.batch.csv.log.comment.CommentLogCSVWriter;
import com.project.forde.batch.csv.log.comment.CommentLogCSVProcessor;
import com.project.forde.batch.csv.log.dto.CSVLogDto;
import com.project.forde.batch.csv.log.like.LikeLogCSVReader;
import com.project.forde.batch.csv.log.like.LikeLogCSVWriter;
import com.project.forde.batch.csv.log.like.LikeLogCSVProcessor;
import com.project.forde.batch.csv.log.view.ViewLogCSVReader;
import com.project.forde.batch.csv.log.view.ViewLogCSVWriter;
import com.project.forde.batch.csv.log.view.ViewLogCsvProcessor;
import com.project.forde.batch.csv.user.dto.InterestTagDto;
import com.project.forde.batch.csv.user.info.UserCSVReader;
import com.project.forde.batch.csv.user.info.UserCSVWriter;
import com.project.forde.batch.csv.user.info.UserCsvProcessor;
import com.project.forde.batch.csv.user.dto.UserDto;
import com.project.forde.batch.csv.user.interest.InterestCSVReader;
import com.project.forde.batch.csv.user.interest.InterestCSVWriter;
import com.project.forde.entity.*;
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
    private final ActivityLogCSVProcessor activityLogCsvProcessor;
    private final ActivityLogCSVWriter activityLogCSVWriter;

    private final LikeLogCSVReader likeLogCSVReader;
    private final LikeLogCSVProcessor likeLogCsvProcessor;
    private final LikeLogCSVWriter likeLogCSVWriter;

    private final CommentLogCSVReader commentLogCSVReader;
    private final CommentLogCSVProcessor commentLogCsvProcessor;
    private final CommentLogCSVWriter commentLogCSVWriter;

    private final ViewLogCSVReader viewLogCSVReader;
    private final ViewLogCsvProcessor viewLogCsvProcessor;
    private final ViewLogCSVWriter viewLogCSVWriter;

    private final UserCSVReader userCSVReader;
    private final UserCsvProcessor userCSVProcessor;
    private final UserCSVWriter userCSVWriter;

    private final InterestCSVReader interestCSVReader;
    private final InterestCSVWriter interestCSVWriter;

    private final BoardCSVReader boardCSVReader;
    private final BoardCsvProcessor boardCSVProcessor;
    private final BoardCSVWriter boardCSVWriter;

    @Bean
    public Job csvJob(
            JobRepository jobRepository,
            Step csvActivityLogStep,
            Step csvLikeStep,
            Step csvCommentStep,
            Step csvViewStep,
            Step csvUserStep,
            Step csvInterestStep,
            Step csvBoardStep
    ) {
        log.info("csvJob 호출");

        return new JobBuilder("csvJob", jobRepository)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(csvActivityLogStep)
                .next(csvLikeStep)
                .next(csvCommentStep)
                .next(csvViewStep)
                .next(csvUserStep)
                .next(csvInterestStep)
                .next(csvBoardStep)
                .build();
    }

    @Bean
    public Step csvActivityLogStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("csvActivityLogStep 호출");

        return new StepBuilder("csvActivityLogStep", jobRepository)
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

    @Bean
    public Step csvUserStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("csvUserStep 호출");

        return new StepBuilder("csvUserStep", jobRepository)
                .<AppUser, UserDto>chunk(100, transactionManager)
                .reader(userCSVReader)
                .processor(userCSVProcessor)
                .writer(userCSVWriter)
                .build();
    }

    @Bean
    public Step csvInterestStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("csvInterestStep 호출");

        return new StepBuilder("csvInterestStep", jobRepository)
                .<InterestTagDto, InterestTagDto>chunk(100, transactionManager)
                .reader(interestCSVReader)
                .writer(interestCSVWriter)
                .build();
    }

    @Bean
    public Step csvBoardStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("csvBoardStep 호출");

        return new StepBuilder("csvBoardStep", jobRepository)
                .<Board, BoardDto>chunk(100, transactionManager)
                .reader(boardCSVReader)
                .processor(boardCSVProcessor)
                .writer(boardCSVWriter)
                .build();
    }
}
