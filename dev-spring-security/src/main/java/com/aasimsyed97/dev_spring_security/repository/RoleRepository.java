package com.aasimsyed97.dev_spring_security.repository;


import com.aasimsyed97.dev_spring_security.model.Role;
import com.aasimsyed97.dev_spring_security.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);
}