package com.alkemy.security.repositories;

import com.alkemy.security.entities.Role;
import com.alkemy.security.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);
}
