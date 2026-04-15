package com.library.shared.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleCustomException() {
        CustomException ex = new CustomException("Test error message");

        ResponseEntity<String> response = exceptionHandler.handle(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test error message", response.getBody());
    }

    @Test
    void testHandleCustomExceptionWithDifferentMessages() {
        CustomException ex1 = new CustomException("Error 1");
        CustomException ex2 = new CustomException("Error 2");

        ResponseEntity<String> response1 = exceptionHandler.handle(ex1);
        ResponseEntity<String> response2 = exceptionHandler.handle(ex2);

        assertEquals("Error 1", response1.getBody());
        assertEquals("Error 2", response2.getBody());
    }

    @Test
    void testHandleCustomExceptionStatusCode() {
        CustomException ex = new CustomException("Bad request");

        ResponseEntity<String> response = exceptionHandler.handle(ex);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testHandleNullMessage() {
        CustomException ex = new CustomException(null);

        ResponseEntity<String> response = exceptionHandler.handle(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testHandleEmptyMessage() {
        CustomException ex = new CustomException("");

        ResponseEntity<String> response = exceptionHandler.handle(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody());
    }
}
