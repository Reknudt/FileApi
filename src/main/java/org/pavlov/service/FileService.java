package org.pavlov.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.dto.response.PageFileResponse;
import org.pavlov.exception.FileNotFoundException;
import org.pavlov.exception.ResourceNotFoundException;
import org.pavlov.mapper.FileMapper;
import org.pavlov.mapper.FileVersionMapper;
import org.pavlov.model.File;
import org.pavlov.model.FileStatus;
import org.pavlov.model.FileVersion;
import org.pavlov.model.User;
import org.pavlov.repository.FileRepository;
import org.pavlov.repository.FileVersionRepository;
import org.pavlov.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.pavlov.util.Constant.ERROR_FORBIDDEN;
import static org.pavlov.util.Constant.ERROR_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FileVersionRepository fileVersionRepository;
    private final FileMapper fileMapper;
    private final FileVersionMapper fileVersionMapper;
    private final UserRepository userRepository;

    public PageFileResponse getFileContentByPage(Long fileId, int pageSize, int pageNumber, String keycloakId) {
        checkFileAccess(fileId, keycloakId);

        byte[] fileContent = findByIdOrThrow(fileId).getData();

        String fileContentString = new String(fileContent, StandardCharsets.UTF_8);
        int totalPages = (int) Math.ceil((double) fileContentString.length() / pageSize);
        if (pageNumber < 1 || pageNumber > totalPages) {
            throw new RuntimeException("Invalid page number");
        }

        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, fileContentString.length());
        String pageContentString = fileContentString.substring(start, end);
        return new PageFileResponse(pageContentString, pageNumber, totalPages);
    }

    public File saveFile(MultipartFile file, Optional<LocalDateTime> dateTime, String keycloakId) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setData(file.getBytes());
        fileEntity.setType(file.getContentType());
        fileEntity.setDateOfCreation(dateTime.orElse(LocalDateTime.now()));

        User currentUser = findUserByKeycloakIdOrThrow(keycloakId);
        fileEntity.setUsers(List.of(currentUser));

        return fileRepository.save(fileEntity);
    }

    public void checkFileAccess(Long fileId, String keycloakUserId) {
        File file = findByIdOrThrow(fileId);
        boolean hasAccess = file.getUsers().stream()
                .anyMatch(user -> user.getKeycloakId().equals(keycloakUserId));

        if (!hasAccess) {
            throw new SecurityException(ERROR_FORBIDDEN );
        }
    }

    @Transactional
    public void assignUser(Long id, Long userId, String keycloakId) {
        checkFileAccess(id, keycloakId);

        File file = findByIdOrThrow(id);
        User newUser = findUserByIdOrThrow(userId);

        List<User> userList = file.getUsers();
        userList.add(newUser);
        file.setUsers(userList);
        fileRepository.save(file);
    }

    @Transactional
    public void removeUser(Long id, Long userId, String keycloakId) {
        checkFileAccess(id, keycloakId);

        File file = findByIdOrThrow(id);
        User newUser = findUserByIdOrThrow(userId);

        List<User> userList = file.getUsers();
        userList.remove(newUser);
        file.setUsers(userList);
        fileRepository.save(file);
    }

    public File getFile(Long id, String keycloakId) {
        checkFileAccess(id, keycloakId);
        return findByIdOrThrow(id);
    }

    public List<FileInfoDto> getFilesByUserId(Long userId) {
        return fileMapper.toFileInfoDto(fileRepository.findFilesByUserId(userId));
    }

    public FileInfoDto getFileInfo(Long id) {
        File file = findByIdOrThrow(id);
        return fileMapper.entityToFileInfoDto(file);
    }

    public Page<FileInfoDto> getAllFiles(Pageable pageable) {
        Page<File> files = fileRepository.findAll(pageable);
        return files.map(fileMapper::entityToFileInfoDto);
    }

    @Transactional
    public void deleteFile(Long id, String keycloakId) {
        checkFileAccess(id, keycloakId);
        File file = findByIdOrThrow(id);
        file.setStatus(FileStatus.DELETED);
        fileRepository.save(file);
//        fileRepository.deleteById(id);
    }

    @Transactional
    public void deleteFileVersion(Long id, long version, String keycloakId) {
        checkFileAccess(id, keycloakId);
        fileVersionRepository.deleteByFileIdAndVersion(id, version);
    }

    @Transactional
    public void deleteFileVersions(Long id, String keycloakId) {
        checkFileAccess(id, keycloakId);
        fileVersionRepository.deleteByFileId(id);
    }

    @Transactional
    public void deleteAll(Long id, String keycloakId) {
        checkFileAccess(id, keycloakId);
        fileVersionRepository.deleteByFileId(id);
        fileRepository.deleteById(id);
    }

    public void updateFileContentOnPage(Long id, int pageNumber, int pageSize, Optional<String> note, String newContent, String keycloakId) {
        checkFileAccess(id, keycloakId);

        File file = findByIdOrThrow(id);

        if (note.isPresent()) {
            file.setNote(note.get());
        } else file.setNote("File content updated");

        byte[] currentContent = file.getData();
        String currentContentString = new String(currentContent, StandardCharsets.UTF_8);
        List<String> pages = splitContentIntoPages(currentContentString, pageSize);
        pages.set(pageNumber - 1, newContent);
        String updatedContent = String.join("", pages);

        byte[] updatedContentBytes = updatedContent.getBytes(StandardCharsets.UTF_8);
        file.setData(updatedContentBytes);
        file.setVersion(file.getVersion() + 1);
        fileRepository.save(file);

        fileVersionRepository.save(fileVersionMapper.fileToFileVersion(file));
    }

    private List<String> splitContentIntoPages(String content, int pageSize) {
        List<String> pages = new ArrayList<>();
        for (int i = 0; i < content.length(); i += pageSize) {
            int end = Math.min(i + pageSize, content.length());
            pages.add(content.substring(i, end));
        }
        return pages;
    }

    public void updateFileName(Long id, Optional<String> note, String newFileName, String keycloakId) {
        checkFileAccess(id, keycloakId);

        File file = findByIdOrThrow(id);
        if (note.isPresent()) {
            file.setNote(note.get());
        } else file.setNote("File name updated");
        file.setName(newFileName);
        file.setVersion(file.getVersion() + 1);
        fileRepository.save(file);
        fileVersionRepository.save(fileVersionMapper.fileToFileVersion(file));
    }

    public void restoreFileVersion(Long id, Long versionId, String keycloakId) {
        checkFileAccess(id, keycloakId);

        FileVersion fileVersionEntity = fileVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        File fileEntity = findByIdOrThrow(id);
        fileEntity = fileMapper.fileVersionToFile(fileVersionEntity);

        fileEntity.setVersion(fileEntity.getVersion() + 1);
        fileEntity.setDateOfCreation(LocalDateTime.now());
        fileRepository.save(fileEntity);
    }

    public void restoreFile(Long id, String keycloakId) {
        checkFileAccess(id, keycloakId);

        File file = findByIdOrThrow(id);
        file.setStatus(FileStatus.OK);
        fileRepository.save(file);
    }

    File findByIdOrThrow(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(
                        () -> new FileNotFoundException(ERROR_NOT_FOUND, Long.toString(id)));
    }

    User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User " + id + " not found"));
    }

    User findUserByKeycloakIdOrThrow(String id) {
        return userRepository.findByKeycloakId(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User with keycloakId " + id + " not found"));
    }
}