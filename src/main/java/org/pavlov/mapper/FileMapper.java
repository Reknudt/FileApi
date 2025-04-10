package org.pavlov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.model.File;
import org.pavlov.model.FileVersion;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileMapper {
    FileInfoDto entityToFileInfoDto(File file);

    List<FileInfoDto> toFileInfoDto(List<File> file);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "dateOfCreation", ignore = true)
    File fileVersionToFile(FileVersion fileVersion);
}
