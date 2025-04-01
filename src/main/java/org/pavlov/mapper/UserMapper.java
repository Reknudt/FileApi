package org.pavlov.mapper;

import org.pavlov.model.User;


public interface UserMapper {

    User updateUserFromEntity(User user);
}