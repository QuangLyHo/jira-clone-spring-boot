package com.example.database_normalization.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.database_normalization.entity.Task;
import com.example.database_normalization.repository.TaskRepository;

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAllWithAssignees();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
}
