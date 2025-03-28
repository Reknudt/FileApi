package org.pavlov.service;


import lombok.RequiredArgsConstructor;
import org.pavlov.exception.FileNotFoundException;
import org.pavlov.model.File;
import org.pavlov.repository.FileRepository;
import org.pavlov.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.pavlov.util.Constant.ERROR_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public File saveFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setData(file.getBytes());
        fileEntity.setType(file.getContentType());
//        Optional<Long> userId = userRepository.findByName(name);
//        if(!userId.isEmpty()) {
//            fileEntity.setUserId(userId.get());
//        }
        return fileRepository.save(fileEntity);
    }

//    public File saveFile(MultipartFile file, String name) throws IOException {
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        File fileEntity = new File();
//        fileEntity.setName(fileName);
//        fileEntity.setData(file.getBytes());
//        fileEntity.setType(file.getContentType());
//        Optional<Long> userId = userRepository.findByName(name);
//        if(!userId.isEmpty()) {
//            fileEntity.setUserId(userId.get());
//        }
//        return fileRepository.save(fileEntity);
//    }

    public File getFile(Long id) {
        File file = findByIdOrThrow(id);
        return file;
    }

    public void deleteFile(Long id) {
        File file = findByIdOrThrow(id);
        fileRepository.deleteById(id);
    }

    private File findByIdOrThrow(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(
                        () -> new FileNotFoundException(ERROR_NOT_FOUND, Long.toString(id)));
    }
}