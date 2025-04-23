package org.pavlov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.pavlov.model.User;
import org.pavlov.service.FileService;
import org.pavlov.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User API")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user info by id")
    public User getByUserID(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all users", description = "Provide all the users")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class))))
    public List<User> getAllUsersInfo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    public void createUser(@RequestBody @Valid User userRequest) {
        userService.createUser(userRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
    public void updateUser(@PathVariable Long id, @RequestBody @Valid User userRequest, @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        userService.updateUser(id, userRequest, keycloakId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user", description = "Provide user id to delete")
    @ApiResponse(responseCode = "204", description = "User deleted", content = @Content)
    public void deleteUserByID(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        userService.deleteUser(id, keycloakId);
    }
}