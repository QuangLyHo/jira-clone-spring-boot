package com.example.database_normalization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.database_normalization.entity.User;
import com.example.database_normalization.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    public UserService userService;

    @Test
    void getAllUsers_delegatesToRepositoryAndReturnsResult() {
        User user = new User();

        user.setEmail("Greg@testEmail.com");
        user.setFirstName("Greg");
        user.setLastName("Hardy");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("Greg@testEmail.com");
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_whenUserExists_returnsUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Greg@testEmail.com");
        user.setFirstName("Greg");
        user.setLastName("Hardy");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("Greg@testEmail.com");
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_whenUserDoesNotExist_returnsEmptyOptional() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        assertThat(result).isEmpty();
        verify(userRepository).findById(99L);
    }

    @Test
    void updateUser_whenUserExists_updatesAndReturnsUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("Greg@testEmail.com");
        existingUser.setFirstName("Greg");
        existingUser.setLastName("Hardy");

        User updatedDetails = new User();
        updatedDetails.setFirstName("newFirst");
        updatedDetails.setLastName("newLast");
        updatedDetails.setEmail("newEmail@Email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        Optional<User> result = userService.updateUser(1L, updatedDetails);
        
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("newFirst");
        assertThat(result.get().getLastName()).isEqualTo("newLast");
        assertThat(result.get().getEmail()).isEqualTo("newEmail@Email.com");
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_whenUserDoesNotExist_returnsEmptyOptional() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(99L, new User());

        assertThat(result).isEmpty();
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_whenUserExists_deletesAndReturnsTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertThat(result).isTrue();
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_returnsFalseWithoutDeleting() {
        when(userRepository.existsById(99L)).thenReturn(false);

        boolean result = userService.deleteUser(99L);

        assertThat(result).isFalse();
        verify(userRepository, never()).deleteById(any());
    }
}
