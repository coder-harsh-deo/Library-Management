package com.library.user.api;

import com.library.user.internal.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    // 🔒 ADMIN ONLY
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return service.getAllUsers();
    }

    // 🔒 ADMIN ONLY
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO getUserById(@PathVariable Long id) {
        return service.getById(id);
    }

    // 🔒 LOGGED-IN USER
    @GetMapping("/me")
    public UserDTO getCurrentUser(@AuthenticationPrincipal(expression = "username") String email) {
        return service.getCurrentUser(email);
    }
}