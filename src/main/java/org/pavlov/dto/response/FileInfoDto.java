package org.pavlov.dto.response;

import org.pavlov.model.FileStatus;
import org.pavlov.model.User;

import java.time.LocalDateTime;
import java.util.List;

public record FileInfoDto(
        Long id,
        String name,
//        byte[] data,
        String type,
        LocalDateTime dateOfCreation,
        long version,
        FileStatus status,
        List<User> users
) {}
