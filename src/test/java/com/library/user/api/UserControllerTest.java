package com.library.user.api;

import com.library.user.domain.Role;
import com.library.user.internal.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUserDto;
    private UserDTO adminUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .role(Role.USER)
                .build();

        adminUserDto = UserDTO.builder()
                .id(2L)
                .name("Admin User")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUserDto, adminUserDto));

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Admin User"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllUsersForbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllUsersUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        when(userService.getById(1L)).thenReturn(testUserDto);

        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserByIdNotFound() throws Exception {
        when(userService.getById(999L)).thenThrow(new RuntimeException("User not found"));

        assertThrows(ServletException.class, () -> mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void testGetCurrentUser() throws Exception {
        when(userService.getCurrentUser("john@example.com")).thenReturn(testUserDto);

        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).getCurrentUser("john@example.com");
    }

    @Test
    void testGetCurrentUserUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanAccessAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUserDto));

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCanAccessOnlyOwnProfile() throws Exception {
        when(userService.getCurrentUser("user@example.com")).thenReturn(testUserDto);

        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
