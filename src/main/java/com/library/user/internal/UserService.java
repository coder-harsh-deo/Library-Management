package com.library.user.internal;

import com.library.shared.exception.CustomException;
import com.library.user.api.UserDTO;
import com.library.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 🔥 HERE
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    public List<UserDTO> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        return toDTO(user);
    }

    public UserDTO getCurrentUser(String email) {
        return toDTO(findByEmail(email));
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}