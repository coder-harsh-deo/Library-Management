package com.library.user.internal;

import com.library.shared.exception.CustomException;
import com.library.user.api.UserDTO;
import com.library.user.domain.Role;
import com.library.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(repository, repository, passwordEncoder);
    }

    @Test
    void testSaveUser() {
        User user = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("plainpassword")
                .role(Role.USER)
                .build();

        String encodedPassword = "$2a$10$encodedpassword";
        when(passwordEncoder.encode("plainpassword")).thenReturn(encodedPassword);

        User savedUser = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        when(repository.save(any(User.class))).thenReturn(savedUser);

        User result = service.saveUser(user);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testFindByEmail() {
        String email = "john@example.com";
        User user = User.builder()
                .name("John Doe")
                .email(email)
                .role(Role.USER)
                .build();

        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = service.findByEmail(email);

        assertEquals(email, result.getEmail());
        verify(repository, times(1)).findByEmail(email);
    }

    @Test
    void testFindByEmailNotFound() {
        String email = "noexist@example.com";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.findByEmail(email));
    }

    @Test
    void testGetAllUsers() {
        User user1 = User.builder()
                .name("User 1")
                .email("user1@example.com")
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .name("User 2")
                .email("user2@example.com")
                .role(Role.ADMIN)
                .build();

        when(repository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDTO> result = service.getAllUsers();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        Long userId = 1L;
        User user = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .role(Role.USER)
                .build();

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        UserDTO result = service.getById(userId);

        assertEquals("john@example.com", result.getEmail());
        verify(repository, times(1)).findById(userId);
    }

    @Test
    void testGetByIdNotFound() {
        Long userId = 999L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.getById(userId));
    }

    @Test
    void testGetCurrentUser() {
        String email = "john@example.com";
        User user = User.builder()
                .name("John Doe")
                .email(email)
                .role(Role.USER)
                .build();

        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDTO result = service.getCurrentUser(email);

        assertEquals(email, result.getEmail());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testPasswordEncoded() {
        User user = User.builder()
                .name("John")
                .email("john@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        String encodedPassword = "encoded_password_hash";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals(encodedPassword, savedUser.getPassword());
            return savedUser;
        });

        service.saveUser(user);

        verify(passwordEncoder).encode("password123");
    }
}
