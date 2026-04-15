package com.library.auth.internal;

import com.library.user.domain.Role;
import com.library.user.domain.User;
import com.library.user.internal.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void testLoadUserByUsername() {
        String email = "test@example.com";
        User user = User.builder()
                .name("Test User")
                .email(email)
                .password("hashedpassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("hashedpassword", userDetails.getPassword());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoadUserByUsernameWithAdminRole() {
        String email = "admin@example.com";
        User adminUser = User.builder()
                .name("Admin User")
                .email(email)
                .password("hashedpassword")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(adminUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsernameUserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
    }

    @Test
    void testLoadUserByUsernameReturnsUserPrincipal() {
        String email = "user@example.com";
        User user = User.builder()
                .name("User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertTrue(userDetails instanceof UserPrincipal);
    }

    @Test
    void testLoadUserByUsernameVerifiesRepositoryCall() {
        String email = "test@example.com";
        User user = User.builder().email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userDetailsService.loadUserByUsername(email);

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, only()).findByEmail(email);
    }
}
