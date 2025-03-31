package org.pavlov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.model.File;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileMapper {

    FileInfoDto toFileInfoDto(File file);

    List<FileInfoDto> toFileInfoDto(List<File> file);
}
