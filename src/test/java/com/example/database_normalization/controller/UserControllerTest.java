package com.example.database_normalization.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.example.database_normalization.dto.UserRequest;
import com.example.database_normalization.dto.UserResponse;
import com.example.database_normalization.security.JwtService;
import com.example.database_normalization.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getAllUsers_returnsJsonArrayOfUsers() throws Exception {
        UserResponse user = new UserResponse(1L, "new@gmail.com", "first", "last");
        Page<UserResponse> page = new PageImpl<>(List.of(user));

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("new@gmail.com"));
    }

    @Test
    void createUser_returnsCreatedUser() throws Exception {
        UserRequest request = new UserRequest("new@gmail.com", "first", "last");
        UserResponse response = new UserResponse(1L, "new@gmail.com", "first", "last");

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@gmail.com"));
    }

    @Test
    void getUserById_whenUserExists_returnsUser() throws Exception {
        UserResponse user = new UserResponse(1L, "Greg@testEmail.com", "first", "last");

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("Greg@testEmail.com"));
    }

    @Test
    void createUser_withBlankEmail_returns400WithFieldError() throws Exception {
        UserRequest invalidRequest = new UserRequest("", "First", "Last");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("email is required"));
    }

    @Test
    void getUserById_whenUserDoesNotExist_returns404() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_whenUserExists_returnsUpdatedUser() throws Exception {
        UserRequest request = new UserRequest("newEmail@gmail.com", "Greg", "Hardy");
        UserResponse response = new UserResponse(1L, "newEmail@gmail.com", "Greg", "Hardy");

        when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)    
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newEmail@gmail.com"));
    }

    @Test
    void updateUser_whenUserDoesNotExist_returns404() throws Exception {
        UserRequest request = new UserRequest("valid@gmail.com", "First", "Last");

        when(userService.updateUser(eq(99L), any(UserRequest.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_whenUserExists_returns204() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_whenUserDoesNotExist_returns404() throws Exception {
        when(userService.deleteUser(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
