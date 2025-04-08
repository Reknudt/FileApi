package org.pavlov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.pavlov.model.User;
import org.pavlov.service.UserService;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
    public void createEmployee(@RequestBody @Valid User userRequest) {
        userService.createUser(userRequest);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('user')")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
    public void updateEmployee(@PathVariable Long id, @RequestBody @Valid User userRequest) {
        userService.updateUser(id, userRequest);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Provide all the users")
    public List<User> getAllUsersInfo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user info by id")
    public User getByUserID(@PathVariable Long id) {
        return userService.getUser(id);
    }

//    @PutMapping("/{id}/assignUser")
//    @Operation(summary = "Assign user to file", description = "Provide file `id` and user `id` to assign")
//    @ApiResponse(responseCode = "200", description = "File access updated", content = @Content)
//    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
//    public void assignUser(@PathVariable Long id, @RequestParam @Valid Long userId) {
//        userService.assignUser(id, userId);
//    }
//
//    @PutMapping("{id}/removeUser")
//    @Operation(summary = "Remove user from file", description = "Provide file `id` and user `id` to remove")
//    @ApiResponse(responseCode = "200", description = "File access updated", content = @Content)
//    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
//    public void removeUser(@PathVariable Long id, @RequestParam @Valid Long userId) {
//        userService.removeUser(id, userId);
//    }

//    @GetMapping("tasks/{id}")
//    @Operation(summary = "Get files by user id")
//    @ApiResponse(responseCode = "200", description = "Request succeed", content = @Content)
//    public Optional<List<File>> getEmployeeTasksByID(@PathVariable Long id) {
//        return userService.getUserFiles(id);
//    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user", description = "Provide user id to delete")
    @ApiResponse(responseCode = "204", description = "User deleted", content = @Content)
    public void deleteUserByID(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}