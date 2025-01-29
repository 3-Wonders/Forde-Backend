package com.project.forde.mapper;

import com.project.forde.entity.ActivityLog;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ActivityLogMapper {
    ActivityLogMapper INSTANCE = Mappers.getMapper(ActivityLogMapper.class);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "board", target = "board")
    @Mapping(source = "duration", target = "duration")
    @Mapping(target = "logType", expression = "java(com.project.forde.type.LogTypeEnum.DURATION)")
    @Mapping(target = "createdTime", ignore = true)
    ActivityLog toDurationEntity(
            AppUser user,
            Board board,
            Long duration
    );

    @Mapping(source = "user", target = "user")
    @Mapping(source = "keyword", target = "keyword")
    @Mapping(target = "logType", expression = "java(com.project.forde.type.LogTypeEnum.SEARCH)")
    @Mapping(target = "createdTime", ignore = true)
    ActivityLog toSearchEntity(
            AppUser user,
            String keyword
    );

    @Mapping(source = "user", target = "user")
    @Mapping(source = "board", target = "board")
    @Mapping(source = "revisitCount", target = "revisitCount")
    @Mapping(target = "logType", expression = "java(com.project.forde.type.LogTypeEnum.REVISIT)")
    @Mapping(target = "createdTime", ignore = true)
    ActivityLog toRevisitEntity(
            AppUser user,
            Board board,
            Long revisitCount
    );
}
