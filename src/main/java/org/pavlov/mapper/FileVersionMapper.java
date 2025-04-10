package org.pavlov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.dto.response.FileVersionInfoDto;
import org.pavlov.model.File;
import org.pavlov.model.FileVersion;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileVersionMapper {
    @Mapping(target = "fileId", source = "file.id")
    FileVersion fileToFileVersion(File file);

    FileVersionInfoDto entityToFileVersionInfoDto(FileVersion fileVersion);
}
