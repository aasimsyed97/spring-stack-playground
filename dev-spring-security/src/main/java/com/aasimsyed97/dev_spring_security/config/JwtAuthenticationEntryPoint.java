package com.aasimsyed97.dev_spring_security.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Decision Point 1: ObjectMapper as instance field
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        // Decision Point 2: Logging the security event
        logAuthenticationFailure(request, authException);

        // Decision Point 3: Set response headers
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));


        // Decision Point 4: Build error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", 401);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Authentication required to access this resource");

        // Decision Point 5: Add request path for debugging
        errorResponse.put("path", request.getServletPath());

        // Decision Point 6: Add specific error type
        errorResponse.put("errorType", determineErrorType(authException));

        // Decision Point 7: Write response
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    // Decision Point 8: Error type determination
    private String determineErrorType(AuthenticationException authException) {
        // Could be extended with custom exceptions
        if (authException.getMessage().contains("expired")) {
            return "TOKEN_EXPIRED";
        } else if (authException.getMessage().contains("invalid")) {
            return "INVALID_TOKEN";
        } else if (authException.getMessage().contains("disabled")) {
            return "ACCOUNT_DISABLED";
        } else if (authException.getMessage().contains("locked")) {
            return "ACCOUNT_LOCKED";
        }
        return "AUTHENTICATION_FAILED";
    }

    // Decision Point 9: Security event logging
    private void logAuthenticationFailure(HttpServletRequest request,
                                          AuthenticationException authException) {
        // In production, use proper logging framework
        System.err.println("Authentication failed for request: " +
                request.getServletPath() +
                " from IP: " + request.getRemoteAddr() +
                " Reason: " + authException.getMessage());
    }
}