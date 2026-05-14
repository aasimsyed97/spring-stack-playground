package com.aasimsyed97.dev_spring_security.controller;

import com.aasimsyed97.dev_spring_security.dto.AuthResponse;
import com.aasimsyed97.dev_spring_security.dto.ErrorResponse;
import com.aasimsyed97.dev_spring_security.dto.LoginRequest;
import com.aasimsyed97.dev_spring_security.dto.RefreshTokenRequest;
import com.aasimsyed97.dev_spring_security.dto.RegisterRequest;
import com.aasimsyed97.dev_spring_security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Decision Point 13: Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                    "Invalid username or password", "/api/auth/login");

        } catch (DisabledException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN,
                    "Account is disabled. Contact administrator.", "/api/auth/login");

        } catch (LockedException e) {
            return buildErrorResponse(HttpStatus.LOCKED,
                    "Account is locked due to multiple failed attempts. Try again later.",
                    "/api/auth/login");
        }
    }

    // Decision Point 14: Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse authResponse = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);

        } catch (RuntimeException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    e.getMessage(), "/api/auth/register");
        }
    }

    // Decision Point 15: Token refresh endpoint
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token", "/api/auth/refresh");
        }
    }

    // Decision Point 16: Error response builder
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, String message, String path) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}