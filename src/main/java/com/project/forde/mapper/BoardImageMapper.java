package com.project.forde.mapper;

import com.project.forde.dto.FileDto;
import com.project.forde.dto.dummyImage.DummyImageDto;
import com.project.forde.entity.BoardImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = { FileUrlResolver.class })
public interface BoardImageMapper {
    @Mapping(source = "image.size", target = "imageSize")
    @Mapping(source = "image.extension", target = "imageType")
    @Mapping(source = "image.storePath", target = "imagePath")
    @Mapping(target = "createdTime", ignore = true)
    BoardImage toEntityWithoutBoard(FileDto image);

    @Mapping(source = "image.imageId", target = "imageId")
    @Mapping(source = "image.imagePath", target = "path", qualifiedByName = "getDefaultThumbnailPath")
    DummyImageDto.Response.Image toImage(BoardImage image);
}
