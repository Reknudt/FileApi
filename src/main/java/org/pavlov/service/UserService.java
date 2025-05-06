package org.pavlov.service;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.pavlov.exception.ResourceNotFoundException;
import org.pavlov.exception.UserAlreadyExistsException;
import org.pavlov.mapper.UserMapper;
import org.pavlov.model.File;
import org.pavlov.model.User;
import org.pavlov.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.pavlov.util.Constant.CLIENTID;
import static org.pavlov.util.Constant.ERROR_FORBIDDEN;
import static org.pavlov.util.Constant.REALM;

@Service
@AllArgsConstructor
public class UserService {

    private final Keycloak keycloakAdminClient;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileService fileService;

    @Transactional
    public void createUser(User user) {
        List<UserRepresentation> existingUsers = keycloakAdminClient.realm(REALM)
                .users()
                .search(user.getEmail());
        if (!existingUsers.isEmpty() || userRepository.existsByEmail(user.getEmail()))
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");

        UserRepresentation keycloakUser = getUserRepresentation(user);

        Response response = keycloakAdminClient.realm(REALM)
                .users()
                .create(keycloakUser);

        if (response.getStatus() == 409) {
            throw new UserAlreadyExistsException("User with this email already exists in Keycloak");
        } else if (response.getStatus() >= 400) {
            String errorMessage = response.readEntity(String.class);
            throw new RuntimeException("Keycloak error: " + response.getStatus() + " - " + errorMessage);
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(user.getPassword());
        credential.setTemporary(false);

        keycloakAdminClient.realm(REALM)
                .users()
                .get(userId)
                .resetPassword(credential);

        try {
            String clientId = CLIENTID;
            List<ClientRepresentation> foundClients = keycloakAdminClient.realm(REALM)
                    .clients()
                    .findByClientId(clientId);

            if (foundClients.isEmpty()) {
                throw new RuntimeException("Client with clientId " + clientId + " not found");
            }

            String internalClientId = foundClients.get(0).getId();
            ClientResource clientResource = keycloakAdminClient.realm(REALM)
                    .clients()
                    .get(internalClientId);

            RoleRepresentation userRole = clientResource.roles()
                    .get("USER")
                    .toRepresentation();

            keycloakAdminClient.realm(REALM)
                    .users()
                    .get(userId)
                    .roles()
                    .clientLevel(internalClientId)
                    .add(Collections.singletonList(userRole));
        } catch (NotFoundException e) {
            throw new RuntimeException("Failed to assign role: " + e.getMessage(), e);
        }

        user.setKeycloakId(userId);
        userRepository.save(user);
    }

    private static UserRepresentation getUserRepresentation(User user) {
        UserRepresentation keycloakUser = new UserRepresentation();

        if (user.getUsername() != null) {
            keycloakUser.setUsername(user.getUsername());
        } else {
            keycloakUser.setUsername(user.getEmail());
        }
        keycloakUser.setEmail(user.getEmail());
        keycloakUser.setFirstName(user.getFirstName());
        keycloakUser.setLastName(user.getLastName());
        keycloakUser.setEnabled(true);
        keycloakUser.setEmailVerified(true);
        keycloakUser.setRequiredActions(Collections.emptyList());
        return keycloakUser;
    }

    @Transactional
    public void updateUser(Long id, User userRequest, String keycloakId) {
        checkAccess(id, keycloakId);
        User user = findByIdOrThrow(id);
        user = userMapper.updateUserFromEntity(userRequest);
        userRepository.save(user);
    }

    @Transactional
    public User getUser(Long id) {
        return findByIdOrThrow(id);
    }

    public List<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.getContent();
    }

    public void deleteUser(Long id, String keycloakId) {
        checkAccess(id, keycloakId);
        User user = findByIdOrThrow(id);
        List<File> files = user.getFiles();
        for (File file : files) {
            fileService.deleteAll(file.getId(), keycloakId);//
        }

        keycloakAdminClient.realm(REALM)
                .users()
                .delete(user.getKeycloakId());
        userRepository.deleteById(id);
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User " + id + " not found"));
    }

//    public User findByKeycloakIdOrThrow(String id) {  //todo: remove
//        return userRepository.findByKeycloakId(id)
//                .orElseThrow(
//                        () -> new ResourceNotFoundException("User with keycloakId " + id + " not found"));
//    }

    public void checkAccess(Long userId, String keycloakUserId) {
        User user = findByIdOrThrow(userId);
        if (!Objects.equals(user.getKeycloakId(), keycloakUserId)) {
            throw new SecurityException(ERROR_FORBIDDEN);
        }
    }
}