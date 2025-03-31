package org.pavlov.service;

import lombok.AllArgsConstructor;
import org.pavlov.exception.ResourceNotFoundException;
import org.pavlov.model.File;
import org.pavlov.model.User;
import org.pavlov.repository.FileRepository;
import org.pavlov.repository.UserRepository;
import org.pavlov.repository.UserRepository;
import org.pavlov.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//    private final FileRepository fileRepository;
//    private final FileService fileService;


    @Transactional
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long id, User userRequest) {
        User user = findByIdOrThrow(id);

        user.setName(userRequest.getName());
        user.setPassword(userRequest.getPassword());
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());
        user.setDateOfBirth(userRequest.getDateOfBirth());

        userRepository.save(user);
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

    @Transactional
    public User getUser(Long id) {
        return findByIdOrThrow(id);
    }

    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .toList();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

//    public Optional<List<Task>> getUserTasks(Long id) {
//        User user = findByIdOrThrow(id);
//        List<Task> tasks = user.getTasks();
//
//        return Optional.ofNullable(user);
//    }
//
//

//    @jakarta.transaction.Transactional
//    public void assignUser(Long id, Long userId) {
//        File file = fileService.findByIdOrThrow(id);
//        User newUser = findByIdOrThrow(userId);
//
//        List<User> userList = file.getUsers();
//        userList.add(newUser);
//        file.setUsers(userList);
//        fileRepository.save(file);
//    }
//
//    @jakarta.transaction.Transactional
//    public void removeUser(Long id, Long userId) {
//        File file = fileService.findByIdOrThrow(id);
//        User newUser = findByIdOrThrow(userId);
//
//        List<User> userList = file.getUsers();
//        userList.remove(newUser);
//        file.setUsers(userList);
//        fileRepository.save(file);
//    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User " + id + " not found"));
    }
}