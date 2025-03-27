package org.pavlov.service;

import lombok.AllArgsConstructor;
import org.pavlov.exception.ResourceNotFoundException;
import org.pavlov.model.User;
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

    @Transactional
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long id, User userRequest) {
        User user = findByIdOrThrow(id);

        user.setName(userRequest.getName());
        user.setPassword(userRequest.getPassword());
        user.setAge(userRequest.getAge());

        userRepository.save(user);
    }

    @Transactional
    public void updateTaskList(Long id, List<Task> tasks) {
        User employee = findByIdOrThrow(id);
        employee.setTasks(tasks);
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateTasksById(Long id, List<Long> taskIds) {
        User user = findByIdOrThrow(id);
        List<Task> newTasks = new ArrayList<>(List.of());

        for (Long taskId : taskIds) {
            Task task = taskService.findByIdOrThrow(taskId);
            newTasks.add(task);
        }

        user.setTasks(newTasks);
        userRepository.save(user);
    }

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

    public Optional<List<Task>> getUserTasks(Long id) {
        User user = findByIdOrThrow(id);
        List<Task> tasks = user.getTasks();

        return Optional.ofNullable(user);
    }
    

    @Transactional
    public void assignTask(Long id, Long taskId) {
        User employee = findByIdOrThrow(id);
        Task newTask = taskService.findByIdOrThrow(taskId);

        List<Task> taskList = employee.getTasks();
        taskList.add(newTask);
        employee.setTasks(taskList);
        employeeRepository.save(employee);
    }

    @Transactional
    public void removeTask(Long id, Long taskId) {
        User employee = findByIdOrThrow(id);
        Task newTask = taskService.findByIdOrThrow(taskId);

        List<Task> taskList = employee.getTasks();
        taskList.remove(newTask);
        employee.setTasks(taskList);
        employeeRepository.save(employee);
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User " + id + " not found"));
    }
}