package org.pavlov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pavlov.model.User;

import java.util.List;

public record FileInfoDto(
        Long id,
        String name,
//        byte[] data,
        String type,
        List<User> users
) {}
