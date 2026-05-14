package com.aasimsyed97.dev_spring_security.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // Decision Point 1: ObjectMapper instance
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {

        // Decision Point 2: Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Decision Point 3: Log the access denied event
        logAccessDenied(request, authentication);

        // Decision Point 4: Set response headers
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Decision Point 5: Build detailed error response
        Map<String, Object> errorResponse = buildErrorResponse(request, authentication);

        // Decision Point 6: Write response
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private Map<String, Object> buildErrorResponse(HttpServletRequest request,
                                                   Authentication authentication) {
        Map<String, Object> errorResponse = new HashMap<>();

        // Basic error information
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", 403);
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "You don't have permission to access this resource");
        errorResponse.put("path", request.getServletPath());

        // Decision Point 7: Include user information (carefully!)
        if (authentication != null && authentication.isAuthenticated()) {
            errorResponse.put("authenticated", true);
            errorResponse.put("username", authentication.getName());

            // Decision Point 8: Include current roles
            errorResponse.put("yourRoles", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            // Decision Point 9: Optional - required roles (if known)
            String requiredRoles = determineRequiredRoles(request);
            if (requiredRoles != null) {
                // intentional design flaw,
                //  User knows what they need
                //  Better UX (can request access)
                //  Reveals existence of admin role
                // Attacker learns role hierarchy
                errorResponse.put("requiredRoles", requiredRoles);
            }
        } else {
            errorResponse.put("authenticated", false);
        }

        return errorResponse;
    }

    // Decision Point 10: Determine required roles from request
    private String determineRequiredRoles(HttpServletRequest request) {
        // This is a simplified approach
        // In production, you might read from endpoint metadata/annotations
        String path = request.getServletPath();

        if (path.startsWith("/api/admin/")) {
            return "ROLE_ADMIN";
        } else if (path.startsWith("/api/mod/")) {
            return "ROLE_MODERATOR";
        } else if (path.startsWith("/api/user/")) {
            return "ROLE_USER or ROLE_ADMIN";
        }

        return null; // Unknown requirement
    }

    // Decision Point 11: Security audit logging
    private void logAccessDenied(HttpServletRequest request, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "anonymous";
        String roles = authentication != null ?
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", ")) : "none";

        // In production, use proper logging framework
        System.err.println("ACCESS DENIED: User '" + username +
                "' with roles [" + roles +
                "] attempted to access: " + request.getServletPath() +
                " from IP: " + request.getRemoteAddr());
    }
}
