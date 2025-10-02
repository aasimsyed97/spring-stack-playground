package com.aasimsyed97.dev_spring_security.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(unique = true, nullable = false)
     private String username;

     @Column(nullable = false)
     private String password;

     private boolean enabled = true;

     private boolean locked = false;

     private int failedLoginAttempts = 0;

     private LocalDateTime accountExpiryDate;

     private LocalDateTime passwordChangedDate;

     @ElementCollection(fetch = FetchType.EAGER)
     @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
     @Column(name = "role")
     private List<String> roles = new ArrayList<>();

     // Constructors, getters, setters

     public User(String username, String password, String... roles) {
          this.username = username;
          this.password = password;
          this.roles = List.of(roles);
     }

     public User() {}

     public Long getId() {
          return id;
     }

     public void setId(Long id) {
          this.id = id;
     }

     public String getUsername() {
          return username;
     }

     public void setUsername(String username) {
          this.username = username;
     }

     public String getPassword() {
          return password;
     }

     public void setPassword(String password) {
          this.password = password;
     }

     public boolean isEnabled() {
          return enabled;
     }

     public void setEnabled(boolean enabled) {
          this.enabled = enabled;
     }

     public boolean isLocked() {
          return locked;
     }

     public void setLocked(boolean locked) {
          this.locked = locked;
     }

     public int getFailedLoginAttempts() {
          return failedLoginAttempts;
     }

     public void setFailedLoginAttempts(int failedLoginAttempts) {
          this.failedLoginAttempts = failedLoginAttempts;
     }

     public LocalDateTime getAccountExpiryDate() {
          return accountExpiryDate;
     }

     public void setAccountExpiryDate(LocalDateTime accountExpiryDate) {
          this.accountExpiryDate = accountExpiryDate;
     }

     public LocalDateTime getPasswordChangedDate() {
          return passwordChangedDate;
     }

     public void setPasswordChangedDate(LocalDateTime passwordChangedDate) {
          this.passwordChangedDate = passwordChangedDate;
     }

     public List<String> getRoles() {
          return roles;
     }

     public void setRoles(List<String> roles) {
          this.roles = roles;
     }

}