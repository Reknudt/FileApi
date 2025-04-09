package org.pavlov.dto.response;

import org.pavlov.model.User;

import java.time.LocalDateTime;
import java.util.List;

public record FileVersionInfoDto(
        Long id,
        Long fileId,
        String name,
        String type,
        LocalDateTime dateOfCreation,
        long version,
        List<User> users
) {}
