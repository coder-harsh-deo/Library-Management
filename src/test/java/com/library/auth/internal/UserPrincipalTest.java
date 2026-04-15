package com.library.auth.internal;

import com.library.user.domain.Role;
import com.library.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalTest {

    private User testUser;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("hashedpassword")
                .role(Role.USER)
                .build();
        userPrincipal = new UserPrincipal(testUser);
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testGetAuthoritiesAdmin() {
        User adminUser = User.builder()
                .role(Role.ADMIN)
                .build();
        UserPrincipal adminPrincipal = new UserPrincipal(adminUser);

        Collection<? extends GrantedAuthority> authorities = adminPrincipal.getAuthorities();

        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testGetPassword() {
        assertEquals("hashedpassword", userPrincipal.getPassword());
    }

    @Test
    void testGetUsername() {
        assertEquals("test@example.com", userPrincipal.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    void testAllAuthenticated() {
        assertTrue(userPrincipal.isAccountNonExpired());
        assertTrue(userPrincipal.isAccountNonLocked());
        assertTrue(userPrincipal.isCredentialsNonExpired());
        assertTrue(userPrincipal.isEnabled());
    }
}
