package com.aasimsyed97.dev_spring_security.service;

import com.aasimsyed97.dev_spring_security.config.JwtUtil;
import com.aasimsyed97.dev_spring_security.dto.AuthResponse;
import com.aasimsyed97.dev_spring_security.dto.LoginRequest;
import com.aasimsyed97.dev_spring_security.dto.RefreshTokenRequest;
import com.aasimsyed97.dev_spring_security.dto.RegisterRequest;
import com.aasimsyed97.dev_spring_security.model.Role;
import com.aasimsyed97.dev_spring_security.model.RoleType;
import com.aasimsyed97.dev_spring_security.model.User;
import com.aasimsyed97.dev_spring_security.repository.RoleRepository;
import com.aasimsyed97.dev_spring_security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.access.expiration:5}")
    private long accessTokenExpirationMinutes;

    // Decision Point 5: Login flow
    public AuthResponse login(LoginRequest loginRequest) {

        // Step 1: Authenticate credentials
        Authentication authentication = authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        // Step 2: Get UserDetails from authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Step 3: Update last login timestamp
        updateLastLogin(userDetails.getUsername());

        // Step 4: Generate tokens
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Step 5: Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMinutes * 60) // Convert to seconds
                .username(userDetails.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList()))
                .build();
    }

    // Decision Point 6: Registration flow
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {

        // Step 1: Validate unique constraints
        validateNewUser(registerRequest);

        // Step 2: Create user entity
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .isEnabled(true)
                .isAccountNonLocked(true)
                .isAccountNonExpired(true)
                .isCredentialsNonExpired(true)
                .build();

        // Step 3: Assign roles
        Set<Role> roles = determineRoles(registerRequest);
        user.setRoles(roles);

        // Step 4: Save user
        User savedUser = userRepository.save(user);

        // Step 5: Generate tokens
        String accessToken = jwtUtil.generateAccessToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);

        // Step 6: Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMinutes * 60)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(savedUser.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList()))
                .build();
    }

    // Decision Point 7: Token refresh flow
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        // Step 1: Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Step 2: Extract username
        String username = jwtUtil.extractUsername(refreshToken);

        // Step 3: Load fresh user details from database
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Step 4: Generate new tokens
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Step 5: Build response
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMinutes * 60)
                .username(userDetails.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList()))
                .build();
    }

    // Decision Point 8: Authentication helper
    private Authentication authenticateUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Reset failed attempts on successful login
            userRepository.resetFailedAttempts(username);

            return authentication;

        } catch (BadCredentialsException e) {
            // Increment failed attempts
            userRepository.incrementFailedAttempts(username);
            throw new BadCredentialsException("Invalid username or password");

        } catch (DisabledException e) {
            throw new DisabledException("Account is disabled");

        } catch (LockedException e) {
            throw new LockedException("Account is locked due to too many failed attempts");
        }
    }

    // Decision Point 9: Validation logic
    private void validateNewUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
    }

    // Decision Point 10: Role assignment logic
    private Set<Role> determineRoles(RegisterRequest request) {
        Set<Role> roles = new HashSet<>();

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            // Admin creating user with specific roles
            for (String roleName : request.getRoles()) {
                RoleType roleType = RoleType.valueOf(roleName);
                Role role = roleRepository.findByName(roleType)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        } else {
            // Default: New users get ROLE_USER
            Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
            roles.add(userRole);
        }

        return roles;
    }

    // Decision Point 11: Audit updates
    private void updateLastLogin(String username) {
        userRepository.updateLastLogin(username, LocalDateTime.now());
    }

    // Decision Point 12: Logout (optional, for token blacklisting)
    public void logout(String username) {
        // If implementing token blacklist, add token to blacklist here
        SecurityContextHolder.clearContext();
    }
}