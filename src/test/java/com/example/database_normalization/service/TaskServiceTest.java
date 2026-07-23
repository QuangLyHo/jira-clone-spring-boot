package com.example.database_normalization.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.database_normalization.entity.Task;
import com.example.database_normalization.repository.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasks_delegatesToRepositoryAndReturnsResult() {
        Task task = new Task();
        task.setTitle("Fix login bug");
        when(taskRepository.findAllWithAssignees()).thenReturn(List.of(task));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Fix login bug");
        verify(taskRepository).findAllWithAssignees();
    }
    
}
