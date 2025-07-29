package com.project.forde.mapper;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.composite.FollowPK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FollowMapper {
    FollowMapper INSTANCE = Mappers.getMapper(FollowMapper.class);

    @Mapping(source = "following", target = "following")
    @Mapping(source = "follower", target = "follower")
    FollowPK toPK(AppUser following, AppUser follower);
}
