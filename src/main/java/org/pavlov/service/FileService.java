package org.pavlov.service;

import org.pavlov.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    File saveFile(MultipartFile file) throws IOException;

    File getFile(Long id);

    void deleteFile(Long id);
}