package org.pavlov.service;

import lombok.AllArgsConstructor;
import org.pavlov.exception.ResourceNotFoundException;
import org.pavlov.mapper.UserMapper;
import org.pavlov.model.User;
import org.pavlov.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long id, User userRequest) {
        User user = findByIdOrThrow(id);

        user = userMapper.updateUserFromEntity(userRequest);
        userRepository.save(user);
    }

//    @Transactional
//    public void updateUser(Long id, User userRequest) {
//        User user = findByIdOrThrow(id);
//
//        user.setName(userRequest.getName());
//        user.setPassword(userRequest.getPassword());
//        user.setEmail(user.getEmail());
//        user.setPhone(user.getPhone());
//        user.setDateOfBirth(userRequest.getDateOfBirth());
//
//        userRepository.save(user);
//    }

    @Transactional
    public User getUser(Long id) {
        return findByIdOrThrow(id);
    }

    public List<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.getContent();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User " + id + " not found"));
    }
}