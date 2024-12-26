package com.project.forde.mapper;

import com.project.forde.dto.FileDto;
import com.project.forde.dto.dummyImage.DummyImageDto;
import com.project.forde.entity.DummyImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DummyImageMapper {
    DummyImageMapper INSTANCE = Mappers.getMapper(DummyImageMapper.class);

    @Mapping(source = "image.size", target = "imageSize")
    @Mapping(source = "image.extension", target = "imageType")
    @Mapping(source = "image.storePath", target = "imagePath")
    DummyImage toEntity(FileDto image);

    @Mapping(source = "image.imageId", target = "imageId")
    @Mapping(source = "image.imagePath", target = "path")
    DummyImageDto.Response.Image toImage(DummyImage image);
}
