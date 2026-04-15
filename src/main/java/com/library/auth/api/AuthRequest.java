package com.library.auth.api;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}