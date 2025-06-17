package com.backend.TTP.controller;

import com.backend.TTP.dto.AuthRequest;
import com.backend.TTP.dto.AuthResponse;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.AuthService;
import com.backend.TTP.config.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration operations")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", 
               description = "Create a new user account with username and password. Returns JWT token upon successful registration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input data or username already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error - registration failed")
    })
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "User registration data", required = true)
            @Valid @RequestBody AuthRequest request) {
        try {
            if (request.getUsername() == null || request.getPassword() == null) {
                throw new IllegalArgumentException("Username and password are required");
            }
            
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());  
            
            User registeredUser = authService.register(user);
            String token = jwtUtil.generateToken(registeredUser.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", 
               description = "Authenticate user with username and password. Returns JWT token upon successful authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - username and password are required"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error - login failed")
    })
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "User login credentials", required = true)
            @Valid @RequestBody AuthRequest request) {
        try {
            if (request.getUsername() == null || request.getPassword() == null) {
                throw new IllegalArgumentException("Username and password are required");
            }
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(), 
                    request.getPassword()
                )
            );
            
            // Extract username from Authentication
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            
            // Pass username to token generation
            String jwt = jwtUtil.generateToken(username);
            
            return ResponseEntity.ok(new AuthResponse(jwt));
            
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    @Operation(summary = "Test authentication", 
               description = "Simple endpoint to test if authentication is working properly. Returns success message if authenticated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication test passed",
                    content = @Content(schema = @Schema(implementation = String.class, example = "Security test passed!"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Security test passed!");
    }

    @GetMapping("/swagger-info")
    @Operation(summary = "Swagger information", description = "Get Swagger URL information")
    public String swaggerInfo() {
        return "Try these URLs:\n" +
               "- http://localhost:8080/swagger-ui.html\n" +
               "- http://localhost:8080/v3/api-docs\n" +
               "- http://localhost:8080/api/debug/test";
    }
}