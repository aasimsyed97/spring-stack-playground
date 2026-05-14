package com.aasimsyed97.dev_spring_security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Decision Point 10: Role as Enum vs String
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private RoleType name;

    @Column(length = 200)
    private String description;

    // Decision Point 11: Bidirectional relationship
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}