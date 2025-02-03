package org.pavlov.service;

import org.springframework.web.multipart.MultipartFile;

public interface CountService {

    Long countCharsInFileByDivide(MultipartFile file);
}