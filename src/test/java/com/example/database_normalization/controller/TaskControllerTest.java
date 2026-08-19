package com.example.database_normalization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.example.database_normalization.dto.TaskRequest;
import com.example.database_normalization.dto.TaskResponse;
import com.example.database_normalization.entity.TaskStatus;
import com.example.database_normalization.security.JwtService;
import com.example.database_normalization.service.TaskService;

import tools.jackson.databind.ObjectMapper;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;



@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getAllTasks_returnsJsonArrayOfTasks() throws Exception {
        TaskResponse task = new TaskResponse(1L, "new task", TaskStatus.todo, null, Set.of());

        when(taskService.getAllTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("new task"));
    }

    @Test
    void createTask_returnsCreatedTask() throws Exception {
        TaskRequest request = new TaskRequest("new task", TaskStatus.todo, null, null);
        TaskResponse response = new TaskResponse(1L, "new task", TaskStatus.todo, null, Set.of());

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("new task"));
    }

    @Test
    void getTaskById_whenTaskExists_returnsTask() throws Exception {
        TaskResponse task = new TaskResponse(1L, "new task", TaskStatus.todo, null, Set.of());

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
        TaskRequest request = new TaskRequest("new task", TaskStatus.todo, null, Set.of());
        TaskResponse response = new TaskResponse(1L, "new task", TaskStatus.todo, null, Set.of());

        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("new task"));
    }

    @Test
    void updateTask_whenTaskDoesNotExist_returns404() throws Exception {
        TaskRequest request = new TaskRequest("new task", TaskStatus.todo, null, Set.of());
        
        when(taskService.updateTask(eq(99L), any(TaskRequest.class))).thenReturn(Optional.empty());
        
        mockMvc.perform(put("/api/tasks/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
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
        TaskRequest invalidRequest = new TaskRequest("", TaskStatus.todo, null, null);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"));
    }

    @Test 
    void createTask_withNonExistentProjectId_returns400() throws Exception {
        TaskRequest request = new TaskRequest("New Task", TaskStatus.todo, 99L, Set.of());

        when(taskService.createTask(any(TaskRequest.class)))
                .thenThrow(new IllegalArgumentException("Project not found: 99"));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Project not found: 99"));
    }
}
