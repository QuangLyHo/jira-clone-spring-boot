package com.example.database_normalization.dto;

import com.example.database_normalization.entity.TaskStatus;

import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
    @NotNull(message = "status is required")
    TaskStatus status
) {}
