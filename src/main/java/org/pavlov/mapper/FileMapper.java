package org.pavlov.mapper;

//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingConstants;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.dto.response.FileReadDto;
import org.pavlov.model.File;

import java.util.List;

//@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileMapper {
//    @Mapping(source = "users", target = "users")
    FileInfoDto entityToFileInfoDto(File file);

    List<FileInfoDto> toFileInfoDto(List<File> file);

    FileReadDto entityToFileReadDto(File file);
}
