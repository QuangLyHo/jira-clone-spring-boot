package com.example.database_normalization.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.database_normalization.dto.UserRequest;
import com.example.database_normalization.dto.UserResponse;
import com.example.database_normalization.entity.User;
import com.example.database_normalization.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id).map(UserResponse::from);
    }

    public UserResponse createUser(UserRequest request) {
        User user = new User();

        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        
        return UserResponse.from(userRepository.save(user));
    }

    public Optional<UserResponse> updateUser(Long id, UserRequest request) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setEmail(request.email());
            existingUser.setFirstName(request.firstName());
            existingUser.setLastName(request.lastName());

            return UserResponse.from(userRepository.save(existingUser));
        });
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }
}
