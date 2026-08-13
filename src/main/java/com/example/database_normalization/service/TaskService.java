package com.example.database_normalization.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.database_normalization.dto.TaskRequest;
import com.example.database_normalization.dto.TaskResponse;
import com.example.database_normalization.entity.Project;
import com.example.database_normalization.entity.Task;
import com.example.database_normalization.entity.User;
import com.example.database_normalization.repository.ProjectRepository;
import com.example.database_normalization.repository.TaskRepository;
import com.example.database_normalization.repository.UserRepository;

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAllWithAssignees().stream()
                .map(TaskResponse::from)
                .toList();
    }

    public Optional<TaskResponse> getTaskById(Long id) {
        return taskRepository.findById(id).map(TaskResponse::from);
    }

    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectIdWithAssignees(projectId).stream()
                .map(TaskResponse::from)
                .toList();
    }

    public TaskResponse createTask(TaskRequest request) {
        Task task = new Task();

        task.setTitle(request.title());
        task.setStatus(request.status());
        task.setProject(resolveProject(request.projectId()));
        task.setAssignees(resolveAssignees(request.assigneeIds()));

        return TaskResponse.from(taskRepository.save(task));
    }

    public Optional<TaskResponse> updateTask(Long id, TaskRequest request) {
        return taskRepository.findById(id).map(existingTask -> {
            existingTask.setTitle(request.title());
            existingTask.setStatus(request.status());

            existingTask.setProject(resolveProject(request.projectId()));
            existingTask.setAssignees(resolveAssignees(request.assigneeIds()));

            return TaskResponse.from(taskRepository.save(existingTask));
        });
    }

    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }

        taskRepository.deleteById(id);
        return true;
    }

    private Project resolveProject(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
    }

    private Set<User> resolveAssignees(Set<Long> assigneeIds) {
        if (assigneeIds == null || assigneeIds.isEmpty()) {
            return new HashSet<>();
        }

        return new HashSet<>(userRepository.findAllById(assigneeIds));
    }
}

