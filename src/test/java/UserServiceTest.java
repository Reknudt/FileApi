import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;

import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pavlov.model.File;
import org.pavlov.model.User;
import org.pavlov.repository.UserRepository;
import org.pavlov.service.FileService;
import org.pavlov.service.UserService;
import static org.mockito.ArgumentMatchers.anyString;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @Mock
//    private Keycloak keycloakAdminClient;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private FileService fileService;
//
//    @Mock
//    private RealmResource realmResource;
//
//    @Mock
//    private UsersResource usersResource;
//
//    @Mock
//    private UserResource userResource;
//
//    @Mock
//    private ClientsResource clientsResource;
//
//    @Mock
//    private ClientResource clientResource;
//
//    @Mock
//    private RolesResource rolesResource;
//
//    @InjectMocks
//    private UserService userService;
//
//    private final String testKeycloakId = "test-user-123";
//
//    @BeforeEach
//    void setUp() {
//        when(keycloakAdminClient.realm(anyString())).thenReturn(realmResource);
//        when(realmResource.users()).thenReturn(usersResource);
//        when(realmResource.clients()).thenReturn(clientsResource);
//        when(usersResource.get(anyString())).thenReturn(userResource);
//
//        // Настройка для createUser
//        when(clientResource.roles()).thenReturn(rolesResource);
////        when(rolesResource.get(anyString())).thenReturn(roleResource);
////        when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation());
//    }
//
//    @Disabled
//    @Test
//    void createUser_ShouldSaveUserWithKeycloakId() {
//        // Arrange
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password");
//        user.setFirstName("Test");
//        user.setLastName("User");
//
//        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
//        when(clientsResource.findByClientId(anyString())).thenReturn(List.of(new ClientRepresentation()));
//        when(clientsResource.get(anyString())).thenReturn(clientResource);
//
//        // Act
//        userService.createUser(user);
//
//        // Assert
//        assertNotNull(user.getKeycloakId());
//        verify(userRepository).save(user);
//    }
//}