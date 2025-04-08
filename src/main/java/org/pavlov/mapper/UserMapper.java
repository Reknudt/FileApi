package org.pavlov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.pavlov.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User updateUserFromEntity(User user);
}