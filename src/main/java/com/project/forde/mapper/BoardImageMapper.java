package com.project.forde.mapper;

import com.project.forde.entity.Board;
import com.project.forde.entity.BoardImage;
import com.project.forde.entity.DummyImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardImageMapper {
    BoardImageMapper INSTANCE = Mappers.getMapper(BoardImageMapper.class);

    @Mapping(source = "board", target = "board")
    @Mapping(source = "image.imageSize", target = "imageSize")
    @Mapping(source = "image.imageType", target = "imageType")
    @Mapping(source = "image.imagePath", target = "imagePath")
    @Mapping(target = "createdTime", ignore = true)
    BoardImage toEntity(Board board, DummyImage image);
}
