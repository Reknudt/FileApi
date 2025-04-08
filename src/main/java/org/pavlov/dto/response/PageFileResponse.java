package org.pavlov.dto.response;


public record PageFileResponse (
    String content,
    int pageNumber,
    int totalPages
) {}