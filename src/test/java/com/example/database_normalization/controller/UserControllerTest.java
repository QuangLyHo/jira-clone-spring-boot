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
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import com.example.database_normalization.entity.User;
import com.example.database_normalization.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getAllUsers_returnsJsonArrayOfUsers() throws Exception {
        User user = new User();
        user.setEmail("new@gmail.com");
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("new@gmail.com"));
    }

    @Test
    void createUser_returnsCreatedUser() throws Exception {
        User newUser = new User();

        newUser.setEmail("new@gmail.com");
        newUser.setFirstName("firstName");
        newUser.setLastName("lastName");

        when(userService.createUser(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@gmail.com"));
    }

    @Test
    void getUserById_whenUserExists_returnsUser() throws Exception {
        User user = new User();

        user.setEmail("Greg@testEmail.com");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("Greg@testEmail.com"));
    }

    @Test
    void getUserById_whenUserDoesNotExist_returns404() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_whenUserExists_returnsUpdatedUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setEmail("newEmail@gmail.com");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)    
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newEmail@gmail.com"));
    }

    @Test
    void updateUser_whenUserDoesNotExist_returns404() throws Exception {
        when(userService.updateUser(eq(99L), any(User.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User())))
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
