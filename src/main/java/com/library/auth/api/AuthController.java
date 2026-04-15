package com.library.auth.api;

import com.library.auth.internal.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 🔐 LOGIN API → generates JWT token
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        String token = authService.login(request.getEmail(), request.getPassword());

        AuthResponse response = new AuthResponse(token);

        return ResponseEntity.ok(response);
    }

    // 🧪 OPTIONAL → test API (public)
    @GetMapping("/test")
    public String test() {
        return "Auth API working";
    }
}