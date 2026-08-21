package com.example.database_normalization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.example.database_normalization.dto.AuthResponse;
import com.example.database_normalization.dto.LoginRequest;
import com.example.database_normalization.dto.RegisterRequest;
import com.example.database_normalization.entity.User;
import com.example.database_normalization.repository.UserRepository;
import com.example.database_normalization.security.JwtService;



@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_withNewEmail_savesUserWithHashedPassword() {
        RegisterRequest request = new RegisterRequest("new@example.com", "password123", "First", "Last");

        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");

        authService.register(request);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUser.getFirstName()).isEqualTo("First");
        assertThat(savedUser.getLastName()).isEqualTo("Last");
        assertThat(savedUser.getPassword()).isEqualTo("hashed-password");
    }
    
    @Test
    void register_withExistingEmail_throwsIllegalArgumentException() {
        RegisterRequest request = new RegisterRequest("existing@example.com", "password123", "First", "Last");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(new User());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withValidCredentials_returnsToken() {
        LoginRequest request = new LoginRequest("user@example.com", "password123");

        when(jwtService.generateToken("user@example.com")).thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("fake-jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_withInvalidCredentials_throwsBadCredentialsException() {
        LoginRequest request = new LoginRequest("user@example.com", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        
                assertThatThrownBy(() -> authService.login(request))
                        .isInstanceOf(BadCredentialsException.class);
    }
}
