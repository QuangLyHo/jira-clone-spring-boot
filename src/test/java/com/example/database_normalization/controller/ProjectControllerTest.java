package com.example.database_normalization.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.database_normalization.dto.ProjectRequest;
import com.example.database_normalization.dto.ProjectResponse;
import com.example.database_normalization.security.JwtService;
import com.example.database_normalization.service.ProjectService;
import com.example.database_normalization.service.TaskService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getAllProjects_returnsJsonArrayOfProjects() throws Exception {
        ProjectResponse project = new ProjectResponse(1L, "name", new BigDecimal("100.00"));
        Page<ProjectResponse> page = new PageImpl<>(List.of(project));

        when(projectService.getAllProjects(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("name"));
    }

    @Test
    void getProjectById_whenProjectExists_returnsProject() throws Exception {
        ProjectResponse project = new ProjectResponse(1L, "Mobile App v1", new BigDecimal("1000.00"));

        when(projectService.getProjectById(1L)).thenReturn(Optional.of(project));

        mockMvc.perform(get("/api/projects/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("Mobile App v1"));
    
    }

    @Test
    void getProjectById_whenProjectDoesNotExist_returns404() throws Exception {
        when(projectService.getProjectById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/99"))
                        .andExpect(status().isNotFound());
    }

    @Test 
    void createProject_returnsCreatedProject() throws Exception {
        ProjectRequest request = new ProjectRequest("new project", new BigDecimal("100.00"));
        ProjectResponse response = new ProjectResponse(1L, "new project", new BigDecimal("100.00"));

        when(projectService.createProject(any(ProjectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("new project"));
    }

    @Test
    void createProject_withBlankName_returns400WithFieldError() throws Exception {
        ProjectRequest invalidRequest = new ProjectRequest("", new BigDecimal("10.00"));

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("name is required"));
    }

    @Test
    void updateProject_whenProjectExists_returnsUpdatedProject() throws Exception {
        ProjectRequest request = new ProjectRequest("New Project", new BigDecimal("10000.00"));
        ProjectResponse response = new ProjectResponse(1L, "New Project", new BigDecimal("10000.00"));

        when(projectService.updateProject(eq(1L), any(ProjectRequest.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Project"));   
    }

    @Test
    void updatedProject_whenProjectDoesNotExist_returns404() throws Exception {
        ProjectRequest request = new ProjectRequest("Valid name", new BigDecimal("10.00"));

        when(projectService.updateProject(eq(99L), any(ProjectRequest.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/projects/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProject_whenProjectExists_returnTrue() throws Exception {
        when(projectService.deleteProject(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProject_whenProjectDoesNotExist_returns404() throws Exception {
        when(projectService.deleteProject(eq(99L))).thenReturn(false);

        mockMvc.perform(delete("/api/projects/99"))
                .andExpect(status().isNotFound());
    }
}
