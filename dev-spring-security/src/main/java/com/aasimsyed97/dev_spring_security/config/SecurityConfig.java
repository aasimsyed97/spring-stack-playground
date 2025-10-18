package com.aasimsyed97.dev_spring_security.config;


import com.aasimsyed97.dev_spring_security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        prePostEnabled = true,    // Decision Point 1: Method-level security
        securedEnabled = true,    // Decision Point 2: @Secured annotation
        jsr250Enabled = true      // Decision Point 3: JSR-250 annotations
)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    // Decision Point 4: Constructor Injection
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // Decision Point 5: Security Filter Chain - The Core Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Decision Point 6: CORS Configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Decision Point 7: CSRF Configuration
                .csrf(csrf -> csrf.disable()) // Decision explained below

                // Decision Point 8: Session Management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Decision Point 9: Exception Handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )

                // Decision Point 10: Authorization Rules
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Role-based endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/mod/**").hasRole("MODERATOR")

                        // Authenticated endpoints (any logged-in user)
                        .requestMatchers("/api/**").authenticated()

                        // Decision Point 11: Catch-all rule
                        .anyRequest().authenticated()
                )

                // Decision Point 12: Authentication Provider
                .authenticationProvider(authenticationProvider())

                // Decision Point 13: JWT Filter Integration
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Decision Point 14: CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Decision Point 15: Allowed Origins
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",    // React dev server
                "http://localhost:4200",    // Angular dev server
                "https://myproductionapp.com"
        ));

        // Decision Point 16: Allowed Methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Decision Point 17: Allowed Headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));

        // Decision Point 18: Exposed Headers
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Total-Count"
        ));

        // Decision Point 19: Credentials Support
        configuration.setAllowCredentials(true);

        // Decision Point 20: Max Age
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    // Decision Point 21: Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Decision Point 22: UserDetailsService setup
        authProvider.setUserDetailsService(userDetailsService);

        // Decision Point 23: Password Encoder
        authProvider.setPasswordEncoder(passwordEncoder());

        // Decision Point 24: Hide User Not Found Exceptions
        authProvider.setHideUserNotFoundExceptions(false);

        return authProvider;
    }

    // Decision Point 25: Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Decision Point 26: Why BCrypt?
        return new BCryptPasswordEncoder(12); // Strength factor 12
    }

    // Decision Point 27: Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Decision Point 28: Authentication Entry Point
    @Bean
    public JwtAuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    // Decision Point 29: Access Denied Handler
    @Bean
    public JwtAccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }
}