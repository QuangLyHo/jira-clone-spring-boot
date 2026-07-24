package com.example.database_normalization.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import com.example.database_normalization.entity.Task;
import com.example.database_normalization.repository.TaskRepository;
import com.example.database_normalization.entity.TaskStatus;

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
    
    @Test
    void updateTask_whenTaskExists_updatesAndReturnsTask() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old title");
        existingTask.setStatus(TaskStatus.todo);

        Task updatedDetails = new Task();
        updatedDetails.setTitle("New title");
        updatedDetails.setStatus(TaskStatus.in_progress);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        Optional<Task> result = taskService.updateTask(1L, updatedDetails);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("New title");
        assertThat(result.get().getStatus()).isEqualTo(TaskStatus.in_progress);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTask_whenTaskDoesNotExist_returnsEmptyOptional() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());


        Optional<Task> result = taskService.updateTask(99L, new Task());

        assertThat(result).isEmpty();
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_whenTaskExists_deletesAndReturnsTrue() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean result = taskService.deleteTask(1L);

        assertThat(result).isTrue();
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_whenTaskDoesNotExist_returnsFalseWithoutDeleting() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        boolean result = taskService.deleteTask(99L);

        assertThat(result).isFalse();
        verify(taskRepository, never()).deleteById(any());
    }
}
