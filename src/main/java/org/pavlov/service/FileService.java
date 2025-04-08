package org.pavlov.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.dto.response.PageFileResponse;
import org.pavlov.exception.FileNotFoundException;
import org.pavlov.mapper.FileMapper;
import org.pavlov.model.File;
import org.pavlov.model.User;
import org.pavlov.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.pavlov.util.Constant.ERROR_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserService userService;
    private final FileMapper fileMapper;

    public PageFileResponse getFileContentByPage(Long fileId, int pageSize, int pageNumber) {
        byte[] fileContent = findByIdOrThrow(fileId).getData();

        String fileContentString = new String(fileContent, StandardCharsets.UTF_8);
        int totalPages = (int) Math.ceil((double) fileContentString.length() / pageSize);
        if (pageNumber < 1 || pageNumber > totalPages) {
            throw new RuntimeException("Номер страницы неверен");
        }

        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, fileContentString.length());
        String pageContentString = fileContentString.substring(start, end);
        return new PageFileResponse(pageContentString, pageNumber, totalPages);
    }

    public File saveFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setData(file.getBytes());
        fileEntity.setType(file.getContentType());
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

    public List<FileInfoDto> getFilesByUserId(Long userId) {
        return fileMapper.toFileInfoDto(fileRepository.findFilesByUserId(userId));
    }

    public FileInfoDto getFileInfo(Long id) {
        return fileMapper.entityToFileInfoDto(findByIdOrThrow(id));
    }

    public Page<FileInfoDto> getAllFiles(Pageable pageable) {
        Page<File> files = fileRepository.findAll(pageable);
        return files.map(fileMapper::entityToFileInfoDto);
    }

    public void deleteFile(Long id) {
        File file = findByIdOrThrow(id);
        fileRepository.deleteById(id);
    }

    //    @Transactional
//    public void updateTaskList(Long id, List<Task> tasks) {
//        User employee = findByIdOrThrow(id);
//        employee.setTasks(tasks);
//        employeeRepository.save(employee);
//    }

//    @Transactional
//    public void updateTasksById(Long id, List<Long> taskIds) {
//        User user = findByIdOrThrow(id);
//        List<Task> newTasks = new ArrayList<>(List.of());
//
//        for (Long taskId : taskIds) {
//            Task task = taskService.findByIdOrThrow(taskId);
//            newTasks.add(task);
//        }
//
//        user.setTasks(newTasks);
//        userRepository.save(user);
//    }

    public void updateFileContentOnPage(Long fileId, int pageNumber, String newContent, int pageSize) {
        File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        byte[] currentContent = fileEntity.getData();
        String currentContentString = new String(currentContent, StandardCharsets.UTF_8);
        List<String> pages = splitContentIntoPages(currentContentString, pageSize);
        pages.set(pageNumber - 1, newContent);
        String updatedContent = String.join("", pages);

        byte[] updatedContentBytes = updatedContent.getBytes(StandardCharsets.UTF_8);
        fileEntity.setData(updatedContentBytes);
        fileRepository.save(fileEntity);
    }

    private List<String> splitContentIntoPages(String content, int pageSize) {
        List<String> pages = new ArrayList<>();
        for (int i = 0; i < content.length(); i += pageSize) {
            int end = Math.min(i + pageSize, content.length());
            pages.add(content.substring(i, end));
        }
        return pages;
    }

    public void updateFileName(Long id, String newFileName) {
        File fileEntity = findByIdOrThrow(id);
        fileEntity.setName(newFileName);
        fileRepository.save(fileEntity);
    }

    File findByIdOrThrow(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(
                        () -> new FileNotFoundException(ERROR_NOT_FOUND, Long.toString(id)));
    }
}