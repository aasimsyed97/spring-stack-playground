package com.aasimsyed97.dev_spring_security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users") // Decision Point 1: Table naming
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // Decision Point 2: Implement UserDetails

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY) // Decision Point 3: ID strategy
     private Long id;

     @Column(nullable = false, unique = true, length = 50)
     private String username;

     @Column(nullable = false, unique = true, length = 100)
     private String email;

     @Column(nullable = false)
     private String password;

     @Column(name = "full_name")
     private String fullName;

     // Decision Point 4: Account status flags
     @Column(name = "is_enabled")
     private boolean isEnabled = true;

     @Column(name = "is_account_non_locked")
     private boolean isAccountNonLocked = true;

     @Column(name = "is_account_non_expired")
     private boolean isAccountNonExpired = true;

     @Column(name = "is_credentials_non_expired")
     private boolean isCredentialsNonExpired = true;

     // Decision Point 5: Audit fields
     @Column(name = "created_at")
     private LocalDateTime createdAt;

     @Column(name = "updated_at")
     private LocalDateTime updatedAt;

     @Column(name = "last_login_at")
     private LocalDateTime lastLoginAt;

     @Column(name = "failed_login_attempts")
     private int failedLoginAttempts = 0;

     // Decision Point 6: Role relationship
     @ManyToMany(fetch = FetchType.EAGER) // Decision Point 7: Fetch strategy
     @JoinTable(
             name = "user_roles",
             joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "role_id")
     )
     private Set<Role> roles;

     public User(String testuser, String encodedPassword, String user, String admin) {
     }

     // Decision Point 8: Lifecycle callbacks
     @PrePersist
     protected void onCreate() {
          createdAt = LocalDateTime.now();
          updatedAt = LocalDateTime.now();
     }

     @PreUpdate
     protected void onUpdate() {
          updatedAt = LocalDateTime.now();
     }

     // Decision Point 9: UserDetails implementation
     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
          return roles.stream()
                  .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                  .collect(Collectors.toSet());
     }

     @Override
     public String getPassword() {
          return password;
     }

     @Override
     public String getUsername() {
          return username;
     }

     @Override
     public boolean isAccountNonExpired() {
          return isAccountNonExpired;
     }

     @Override
     public boolean isAccountNonLocked() {
          return isAccountNonLocked;
     }

     @Override
     public boolean isCredentialsNonExpired() {
          return isCredentialsNonExpired;
     }

     @Override
     public boolean isEnabled() {
          return isEnabled;
     }

     // Helper method to add role
     public void addRole(Role role) {
          this.roles.add(role);
     }

     // Helper method to remove role
     public void removeRole(Role role) {
          this.roles.remove(role);
     }
}