package com.aasimsyed97.dev_spring_security.service;


import com.aasimsyed97.dev_spring_security.model.User;
import com.aasimsyed97.dev_spring_security.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    // Constants for security policies
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int CREDENTIALS_EXPIRY_DAYS = 90;

    private final UserRepository userRepository;

    // Decision Point 1: Constructor Injection
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Decision Point 2: Core Method - Loading User by Username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Decision Point 3: Username Validation and Sanitization
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Username cannot be empty");
        }

        String sanitizedUsername = username.trim().toLowerCase();

        // Decision Point 4: Database Lookup Strategy
        User user = userRepository.findByUsername(sanitizedUsername)
                .orElseThrow(() -> {
                    // Decision Point 5: Security-Conscious Error Messages
                    logSecurityEvent("Failed login attempt for username: " + sanitizedUsername);
                    return new UsernameNotFoundException(
                            "User not found with username: " + sanitizedUsername);
                });

        // Decision Point 6: Account Status Validation
        validateUserAccountStatus(user);

        // Decision Point 7: Authority/Role Conversion
        Collection<? extends GrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        // Decision Point 8: UserDetails Implementation Choice
        return buildUserDetails(user, authorities);
    }

    // Decision Point 9: Role to Authority Mapping Strategy
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<String> roles) {
        if (roles == null || roles.isEmpty()) {
            // Decision Point 10: Default Role for Users
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return roles.stream()
                .map(role -> {
                    // Decision Point 11: Role Prefixing Strategy
                    String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());
    }

    // Decision Point 12: UserDetails Implementation Decision
    private UserDetails buildUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Already encoded
                .authorities(authorities)
                // Decision Point 13: Account Status Flags
                .accountExpired(isAccountExpired(user))
                .accountLocked(isAccountLocked(user))
                .credentialsExpired(isCredentialsExpired(user))
                .disabled(!user.isEnabled())
                .build();
    }

    // Decision Point 14: Account Status Validation Methods
    private void validateUserAccountStatus(User user) {
        if (!user.isEnabled()) {
            logSecurityEvent("Disabled account access attempt: " + user.getUsername());
            throw new UsernameNotFoundException("Account is disabled");
        }

        if (isAccountExpired(user)) {
            logSecurityEvent("Expired account access attempt: " + user.getUsername());
            throw new UsernameNotFoundException("Account has expired");
        }

        if (isAccountLocked(user)) {
            logSecurityEvent("Locked account access attempt: " + user.getUsername());
            throw new UsernameNotFoundException("Account is locked");
        }

        if (isCredentialsExpired(user)) {
            logSecurityEvent("Expired credentials access attempt: " + user.getUsername());
            throw new UsernameNotFoundException("Credentials have expired");
        }
    }

    // Decision Point 15: Account Expiration Logic
    private boolean isAccountExpired(User user) {
        return user.getAccountExpiryDate() != null &&
                user.getAccountExpiryDate().isBefore(java.time.LocalDateTime.now());
    }

    // Decision Point 16: Account Locking Logic
    private boolean isAccountLocked(User user) {
        return user.isLocked() ||
                (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS);
    }

    // Decision Point 17: Credential Expiration Logic
    private boolean isCredentialsExpired(User user) {
        return user.getPasswordChangedDate() != null &&
                user.getPasswordChangedDate().isBefore(
                        java.time.LocalDateTime.now().minusDays(CREDENTIALS_EXPIRY_DAYS));
    }

    // Decision Point 18: Security Event Logging
    private void logSecurityEvent(String message) {
        // In production, use proper logging framework with security context
        System.out.println("SECURITY EVENT: " + java.time.LocalDateTime.now() + " - " + message);
        // TODO: Integrate with proper logging system (Logback, Log4j2)
        // TODO: Send to security monitoring system
    }

    // Decision Point 19: Additional Business Methods
    public void incrementFailedLoginAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            // Auto-lock account after too many failures
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLocked(true);
                logSecurityEvent("Account auto-locked due to failed attempts: " + username);
            }

            userRepository.save(user);
        });
    }

    public void resetFailedLoginAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLocked(false);
            userRepository.save(user);
        });
    }


}