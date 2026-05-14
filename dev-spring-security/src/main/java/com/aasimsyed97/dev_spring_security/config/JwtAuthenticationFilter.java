package com.aasimsyed97.dev_spring_security.config;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;


    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


        try {
            final String jwtToken = extractJwtFromRequest(request);

            if (jwtToken == null) {
                // No token - let Spring Security handle (might be public endpoint)
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtUtil.validateToken(jwtToken)) {
                handleAuthenticationError(response, "Invalid or expired token");
                return;
            }

            String username = jwtUtil.extractUsername(jwtToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Additional check: Is user still active?
                if (!userDetails.isEnabled()) {
                    handleAuthenticationError(response, "Account is disabled");
                    return;
                }

                // Additional check: Is account locked?
                if (!userDetails.isAccountNonLocked()) {
                    handleAuthenticationError(response, "Account is locked");
                    return;
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handleAuthenticationError(response, "Authentication failed");
        }
    }

    // Decision Point 12: Token Extraction Strategy
    public String extractJwtFromRequest(HttpServletRequest request) {
        // Check Authorization header first
        String bearerToken = request.getHeader("Authorization");

        // Decision Point 13: Authorization Header Format Validation
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }

        // Decision Point 14: Alternative Token Locations
        // Check cookie as fallback (for web applications)
        String cookieToken = extractTokenFromCookie(request);
        if (cookieToken != null) {
            return cookieToken;
        }

        // Decision Point 15: Query parameter fallback (for special cases)
        String queryToken = request.getParameter("token");
        if (queryToken != null && !queryToken.trim().isEmpty()) {
            return queryToken;
        }

        return null;
    }

    // Decision Point 16: Cookie Extraction Implementation
    private String extractTokenFromCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("JWT-TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // Decision Point 17: Bypass Filter for Certain Paths
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // Decision Point 18: Public Endpoints that don't need JWT
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/public/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/actuator/health");
    }

    // Decision Point 19: Error Handling Enhancement
    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }
}