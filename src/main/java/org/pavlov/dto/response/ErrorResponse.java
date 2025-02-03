package org.pavlov.dto.response;

import lombok.Builder;

@Builder
public record ErrorResponse(

        int status,

        String message
) {}