package org.pavlov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.model.File;
import org.pavlov.model.User;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User updateUserFromEntity(User user);
}