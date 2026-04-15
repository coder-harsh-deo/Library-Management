package com.library.auth.internal;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "my-secret-key-that-is-long-enough-for-hmac-sha256-algorithm");
        jwtUtil.init();
    }

    @Test
    void testGenerateToken() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractEmail() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);
        String extractedEmail = jwtUtil.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void testIsTokenValid() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void testIsTokenInvalid() {
        String invalidToken = "invalid.token.string";

        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }

    @Test
    void testTokenWithDifferentEmail() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String token = jwtUtil.generateToken(email1);

        assertEquals(email1, jwtUtil.extractEmail(token));
        assertNotEquals(email2, jwtUtil.extractEmail(token));
    }

    @Test
    void testEmptyTokenInvalid() {
        assertFalse(jwtUtil.isTokenValid(""));
    }

    @Test
    void testNullTokenHandling() {
        assertFalse(jwtUtil.isTokenValid(null));
    }
}
