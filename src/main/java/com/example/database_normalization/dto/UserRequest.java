package com.example.database_normalization.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank(message = "email is required") 
    String email,

    @NotBlank(message = "first name is required")
    String firstName,

    @NotBlank(message = "last name is required")
    String lastName
) {}
