package com.example.database_normalization.dto;

import com.example.database_normalization.entity.User;

public record UserResponse(Long id, String email, String first, String last) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
