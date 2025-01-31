package com.project.forde.mapper;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Tag;
import com.project.forde.entity.composite.InterestTagPK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface InterestTagMapper {
    InterestTagMapper INSTANCE = Mappers.getMapper(InterestTagMapper.class);

    @Mapping(source = "tag", target = "tag")
    @Mapping(source = "appUser", target = "appUser")
    InterestTagPK toPK(Tag tag, AppUser appUser);
}
