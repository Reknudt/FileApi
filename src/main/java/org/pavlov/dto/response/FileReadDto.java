package org.pavlov.dto.response;

import org.pavlov.model.User;

import java.util.List;

public record FileReadDto(
        String data,
        String name,
        String type,
        List<User> users
) {}
