package com.expense.tracker.controller;

import com.expense.tracker.dto.LoginRequestDto;
import com.expense.tracker.dto.LoginResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Initial Primary User Credentials
    private static final String INITIAL_USER_ID = "harini.muhilan";
    private static final String INITIAL_EMAIL = "harini.muhilan@dualpath.com";
    private static final String INITIAL_PASSWORD = "DualPath@Secure2026";
    private static final String INITIAL_NAME = "Harini Muhilan";
    private static final String INITIAL_ROLE = "Executive Administrator";

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(LoginResponseDto.builder()
                            .success(false)
                            .message("Username and password are required.")
                            .build());
        }

        String identifier = request.getUsername().trim().toLowerCase();
        String password = request.getPassword().trim();

        boolean isIdentifierValid = identifier.equals(INITIAL_USER_ID.toLowerCase())
                || identifier.equals(INITIAL_EMAIL.toLowerCase())
                || identifier.equals("harini");

        if (isIdentifierValid && password.equals(INITIAL_PASSWORD)) {
            String token = "dp_sec_" + UUID.randomUUID().toString().replace("-", "");
            LoginResponseDto response = LoginResponseDto.builder()
                    .success(true)
                    .token(token)
                    .username(INITIAL_USER_ID)
                    .email(INITIAL_EMAIL)
                    .name(INITIAL_NAME)
                    .role(INITIAL_ROLE)
                    .message("Authentication successful")
                    .build();
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponseDto.builder()
                        .success(false)
                        .message("Invalid credentials. Please verify your username/email and password.")
                        .build());
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDto> getProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(LoginResponseDto.builder()
                .success(true)
                .token(token != null ? token : "dp_sec_active")
                .username(INITIAL_USER_ID)
                .email(INITIAL_EMAIL)
                .name(INITIAL_NAME)
                .role(INITIAL_ROLE)
                .message("Session active")
                .build());
    }
}
