package com.project.forde.mapper;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.composite.FollowPK;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface FollowMapper {
    FollowMapper INSTANCE = Mappers.getMapper(FollowMapper.class);

    @Mapping(source = "receiver", target = "following")
    @Mapping(source = "sender", target = "follower")
    FollowPK toFollowPK(AppUser sender, AppUser receiver);
}
