package com.example.database_normalization.dto;

import jakarta.validation.constraints.NotBlank;

public record TeamRequest(

    @NotBlank(message = "name is required")
    String name

) {}
