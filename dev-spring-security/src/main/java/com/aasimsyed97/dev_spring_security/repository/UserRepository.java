package com.aasimsyed97.dev_spring_security.repository;

import com.aasimsyed97.dev_spring_security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Decision Point 14: Query methods
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // Decision Point 15: Audit queries
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.username = :username")
    void updateLastLogin(@Param("username") String username,
                         @Param("lastLoginAt") LocalDateTime lastLoginAt);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.username = :username")
    void incrementFailedAttempts(@Param("username") String username);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = 0 WHERE u.username = :username")
    void resetFailedAttempts(@Param("username") String username);
}