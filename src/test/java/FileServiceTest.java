import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pavlov.mapper.FileMapper;
import org.pavlov.mapper.FileVersionMapper;
import org.pavlov.model.File;
import org.pavlov.model.FileStatus;
import org.pavlov.model.FileVersion;
import org.pavlov.model.User;
import org.pavlov.repository.FileRepository;
import org.pavlov.repository.FileVersionRepository;
import org.pavlov.repository.UserRepository;
import org.pavlov.service.FileService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileVersionRepository fileVersionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private FileVersionMapper fileVersionMapper;

    @InjectMocks
    private FileService fileService;

    private final String testKeycloakId = "test-user-123";

    @Test
    void saveFile_ShouldAssignCurrentUser() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getBytes()).thenReturn("content".getBytes());
        when(file.getContentType()).thenReturn("text/plain");

        User user = new User();
        user.setKeycloakId(testKeycloakId);
        when(userRepository.findByKeycloakId(testKeycloakId)).thenReturn(Optional.of(user));

        // Mock сохранение в репозитории
        File expectedFile = new File();
        when(fileRepository.save(any(File.class))).thenReturn(expectedFile);

        // Act
        File savedFile = fileService.saveFile(file, Optional.empty(), testKeycloakId);

        // Assert
        assertNotNull(savedFile);
        verify(fileRepository).save(any(File.class));
    }

    @Test
    void deleteFile_ShouldMarkAsDeleted() {
        // Arrange
        File file = new File();
        file.setId(1L);
        file.setStatus(FileStatus.OK);
        file.setUsers(List.of(createTestUser(testKeycloakId)));

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        // Act
        fileService.deleteFile(1L, testKeycloakId);

        // Assert
        assertEquals(FileStatus.DELETED, file.getStatus());
        verify(fileRepository).save(file);
    }

    @Test
    void updateFileName_ShouldCreateNewVersion() {
        // Arrange
        File file = new File();
        file.setId(1L);
        file.setVersion(1);
        file.setUsers(List.of(createTestUser(testKeycloakId)));

        FileVersion fileVersion = new FileVersion();
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileVersionMapper.fileToFileVersion(file)).thenReturn(fileVersion);

        // Act
        fileService.updateFileName(1L, Optional.empty(), "new-name.txt", testKeycloakId);

        // Assert
        assertEquals("new-name.txt", file.getName());
        assertEquals(2, file.getVersion());
        verify(fileVersionRepository).save(fileVersion);
    }

    @Test
    void checkFileAccess_ShouldThrowWhenNoAccess() {
        // Arrange
        File file = new File();
        file.setUsers(List.of(createTestUser("another-user")));

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        // Act & Assert
        assertThrows(SecurityException.class, () -> 
            fileService.checkFileAccess(1L, testKeycloakId));
    }

    private User createTestUser(String keycloakId) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        return user;
    }
}