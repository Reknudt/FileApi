package org.pavlov.dto.response;

import org.pavlov.model.User;

import java.util.List;

public record FileInfoDto(
        Long id,
        String name,
        String type,
        List<User> users
) {}
