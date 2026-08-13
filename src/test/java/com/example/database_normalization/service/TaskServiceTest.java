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
import java.util.Set;

import com.example.database_normalization.dto.TaskRequest;
import com.example.database_normalization.dto.TaskResponse;
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

        List<TaskResponse> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Fix login bug");
        verify(taskRepository).findAllWithAssignees();
    }

    @Test
    void getAllTaskById_whenTaskExists_returnsTasks() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("New title");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<TaskResponse> result = taskService.getTaskById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("New title");
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_whenTaskDoesNotExist_returnsEmptyOptional() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TaskResponse> result = taskService.getTaskById(99L);

        assertThat(result).isEmpty();
        verify(taskRepository).findById(99L);
    }

    @Test
    void createTask_savesAndReturnsTask() {
        TaskRequest request = new TaskRequest("New Title", TaskStatus.todo, null, Set.of());

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setId(1L);
            return savedTask;
        });

        TaskResponse result = taskService.createTask(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("New Title");
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void updateTask_whenTaskExists_updatesAndReturnsTask() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old title");
        existingTask.setStatus(TaskStatus.todo);

        TaskRequest request = new TaskRequest("New title", TaskStatus.todo, null, Set.of());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        Optional<TaskResponse> result = taskService.updateTask(1L, request);

        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("New title");
        assertThat(result.get().status()).isEqualTo(TaskStatus.todo);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTask_whenTaskDoesNotExist_returnsEmptyOptional() {
        TaskRequest request = new TaskRequest("New title", TaskStatus.done, null, Set.of());
        
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());


        Optional<TaskResponse> result = taskService.updateTask(99L, request);

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
