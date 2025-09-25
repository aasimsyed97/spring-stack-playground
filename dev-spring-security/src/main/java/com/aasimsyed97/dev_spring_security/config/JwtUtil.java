package com.aasimsyed97.dev_spring_security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.expiration:5}")
    private long accessExpirationMinutes;


    @Value("${jwt.refresh.expiration:1440}") //24 hours
    private long refreshExpirationMinutes;

     private Algorithm getSigningAlgorithm(){
         return  Algorithm.HMAC256(secretKey.getBytes());

     }


    public String generateAccessToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer("DevSpringSecurityExamplesApplication")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() +
                        TimeUnit.MINUTES.toMillis(accessExpirationMinutes)))
                .withClaim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(getSigningAlgorithm());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer("DevSpringSecurityExamplesApplication")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() +
                        TimeUnit.MINUTES.toMillis(refreshExpirationMinutes)))
                // Decision: Refresh token doesn't need roles, just identity
                .sign(getSigningAlgorithm());
    }


    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(getSigningAlgorithm())
                    .withIssuer("DevSpringSecurityExamplesApplication") // Decision: Validate issuer
                    .build();
            verifier.verify(token); // This throws exception if invalid
            return !isTokenExpired(token); // Decision: Additional expiration check
        } catch (JWTVerificationException exception) {
            // Decision: Logging vs throwing exception
            System.err.println("JWT validation failed: " + exception.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Decision Point 8: Token Data Extraction
    public String extractUsername(String token) {
        return decodeToken(token).getSubject(); // Decision: Username from subject claim
    }

    public Date extractExpiration(String token) {
        return decodeToken(token).getExpiresAt();
    }

    public String[] extractRoles(String token) {
        return decodeToken(token)
                .getClaim("roles")
                .asArray(String.class);
    }


    private DecodedJWT decodeToken(String token) {
        try {
            return JWT.decode(token); // Decision: Decode without verification first
        } catch (Exception e) {
            throw new JWTVerificationException("Invalid JWT token");
        }
    }


    public boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) ||
                isWithinRefreshWindow(token));
    }

    private boolean isWithinRefreshWindow(String token) {
        final long refreshWindowMinutes = 5;
        Date expiration = extractExpiration(token);
        return expiration.after(new Date(System.currentTimeMillis() -
                TimeUnit.MINUTES.toMillis(refreshWindowMinutes)));
    }


}
