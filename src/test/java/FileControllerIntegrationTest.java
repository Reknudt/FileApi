import org.junit.jupiter.api.Test;
import org.pavlov.Main;
import org.pavlov.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.pavlov.service.FileService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private JwtAuthenticationToken createTestJwt(String subject) {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("sub", subject)
                .build();
        return new JwtAuthenticationToken(jwt, Collections.emptyList(), "test-token");
    }

    @Test
    void uploadFile_ShouldRequireAuthentication() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        JwtAuthenticationToken authentication = createTestJwt("test-user-id");

        when(fileService.saveFile(any(), any(), anyString())).thenReturn(new File());

        // Act & Assert
        mockMvc.perform(multipart("/api/file")
                        .file(file)
                        .param("dateOfCreation", LocalDateTime.now().toString())
                        .with(authentication(authentication)))
                .andExpect(status().isOk());
    }

    @Test
    void updateFileName_ShouldCheckAccess() throws Exception {
        // Arrange
        JwtAuthenticationToken authentication = createTestJwt("test-user-id");
        when(fileService.getFile(anyLong(), anyString())).thenReturn(new File());

        // Act & Assert
        mockMvc.perform(patch("/api/file/1/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"new-name.txt\"}")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());
    }
}