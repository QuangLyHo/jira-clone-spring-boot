package com.example.database_normalization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.database_normalization.entity.Task;
import com.example.database_normalization.service.TaskService;

import tools.jackson.databind.ObjectMapper;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;



@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void getAllTasks_returnsJsonArrayOfTasks() throws Exception {
        Task task = new Task();
        task.setTitle("Fix login bug");
        when(taskService.getAllTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Fix login bug"));
    }

    @Test
    void createTask_returnsCreatedTask() throws Exception {
        Task newTask = new Task();

        newTask.setTitle("New Task");
        when(taskService.createTask(any(Task.class))).thenReturn(newTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void getTaskById_whenTaskExists_returnsTask() throws Exception {
        Task task = new Task();
        task.setTitle("new task");

        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("new task"));
    }

    @Test
    void getTaskById_whenTaskDoesNotExist_returns404() throws Exception {
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_whenTaskExists_returnsUpdatedTask() throws Exception {
        Task updatedTask = new Task();

        updatedTask.setTitle("Updated title");
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(Optional.of(updatedTask));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    @Test
    void updateTask_whenTaskDoesNotExist_returns404() throws Exception {
        Task validTask = new Task();
        validTask.setTitle("Valid title");
        
        when(taskService.updateTask(eq(99L), any(Task.class))).thenReturn(Optional.empty());
        
        mockMvc.perform(put("/api/tasks/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validTask)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteTask_whenTaskExists_returns204() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_whenTaskDoesNotExist_returns404() throws Exception {
        when(taskService.deleteTask(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_withBlankTitle_returns400WithFieldError() throws Exception {
        Task invalidTask = new Task();
        invalidTask.setTitle("");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"));
    }
}
