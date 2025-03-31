package org.pavlov.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.exception.FileNotFoundException;
import org.pavlov.model.File;
import org.pavlov.model.User;
import org.pavlov.repository.FileRepository;
import org.pavlov.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.pavlov.util.Constant.ERROR_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final UserService userService;

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

    @Transactional
    public void assignUser(Long id, Long userId) {
        File file = findByIdOrThrow(id);
        User newUser = userService.findByIdOrThrow(userId);

        List<User> userList = file.getUsers();
        userList.add(newUser);
        file.setUsers(userList);
        fileRepository.save(file);
    }

    @Transactional
    public void removeUser(Long id, Long userId) {
        File file = findByIdOrThrow(id);
        User newUser = userService.findByIdOrThrow(userId);

        List<User> userList = file.getUsers();
        userList.remove(newUser);
        file.setUsers(userList);
        fileRepository.save(file);
    }

    public File getFile(Long id) {
        return findByIdOrThrow(id);
    }

    public List<File> getFilesByUserId(Long userId) {
        return fileRepository.findFilesByUserId(userId);
    }

    public FileInfoDto getFileInfo(Long id) {
//        return fileRepository.findFileInfo(id);
        return null;
    }

    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    public void deleteFile(Long id) {
        File file = findByIdOrThrow(id);
        fileRepository.deleteById(id);
    }

    File findByIdOrThrow(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(
                        () -> new FileNotFoundException(ERROR_NOT_FOUND, Long.toString(id)));
    }
}