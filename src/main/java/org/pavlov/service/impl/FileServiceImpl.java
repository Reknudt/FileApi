package org.pavlov.service.impl;


import lombok.RequiredArgsConstructor;
import org.pavlov.exception.FileNotFoundException;
import org.pavlov.model.File;
import org.pavlov.repository.FileRepository;
import org.pavlov.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.pavlov.util.Constant.ERROR_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;


    @Override
    public File saveFile(MultipartFile file) throws IOException {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setData(file.getBytes());
        fileEntity.setType(file.getContentType());

        return fileRepository.save(fileEntity);
    }

    @Override
    public File getFile(Long id) {
        File file = findByIdOrThrow(id);
        return file;
    }

    @Override
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }

    private File findByIdOrThrow(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(
                        () -> new FileNotFoundException(ERROR_NOT_FOUND, Long.toString(id)));
    }
}