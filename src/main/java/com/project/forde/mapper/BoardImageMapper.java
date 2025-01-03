package com.project.forde.mapper;

import com.project.forde.dto.FileDto;
import com.project.forde.dto.dummyImage.DummyImageDto;
import com.project.forde.entity.BoardImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardImageMapper {
    BoardImageMapper INSTANCE = Mappers.getMapper(BoardImageMapper.class);

    @Mapping(source = "image.size", target = "imageSize")
    @Mapping(source = "image.extension", target = "imageType")
    @Mapping(source = "image.storePath", target = "imagePath")
    @Mapping(target = "createdTime", ignore = true)
    BoardImage toEntityWithoutBoard(FileDto image);

    @Mapping(source = "image.imageId", target = "imageId")
    @Mapping(source = "image.imagePath", target = "path")
    DummyImageDto.Response.Image toImage(BoardImage image);
}
