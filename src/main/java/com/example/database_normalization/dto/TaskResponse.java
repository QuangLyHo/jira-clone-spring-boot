package com.example.database_normalization.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.database_normalization.entity.Task;
import com.example.database_normalization.entity.TaskStatus;

public record TaskResponse(
    Long id, 
    String title, 
    TaskStatus status, 
    ProjectResponse project,
    Set<UserResponse> assignees
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
            task.getId(), 
            task.getTitle(), 
            task.getStatus(), 
            task.getProject() != null ? ProjectResponse.from(task.getProject()) : null,
            task.getAssignees().stream().map(UserResponse::from).collect(Collectors.toSet())
        );
    }
}
