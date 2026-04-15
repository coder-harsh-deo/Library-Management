package com.library.shared.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handle(CustomException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}