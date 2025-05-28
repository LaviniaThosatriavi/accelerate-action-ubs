package com.backend.TTP.controller;

import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.security.JwtUtil;
import com.backend.TTP.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;

    // Single constructor to inject all dependencies
    public AuthController(AuthService authService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        authService.login(user.getUsername(), user.getPassword());
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUserDetails(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found")); // Handle Optional
        return ResponseEntity.ok(user);
    }
}