package com.aasimsyed97.dev_spring_security.repository;

import com.aasimsyed97.dev_spring_security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.username = ?1")
    void incrementFailedAttempts(String username);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = 0 WHERE u.username = ?1")
    void resetFailedAttempts(String username);
}