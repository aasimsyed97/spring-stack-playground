package com.aasimsyed97.dev_spring_security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    // Decision Point 2: Token type for OAuth2 compliance
    @Builder.Default
    private String tokenType = "Bearer";

    // Decision Point 3: Token expiration info
    private long expiresIn; // seconds

    // Decision Point 4: User info in response
    private String username;
    private String email;
    private List<String> roles;
}