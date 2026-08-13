package com.example.database_normalization.dto;

import java.util.Set;

import com.example.database_normalization.entity.TaskStatus;

import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
    
    @NotBlank(message = "Title is required")
    String title,

    TaskStatus status,

    Long projectId,

    Set<Long> assigneeIds

) {}
