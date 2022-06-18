package com.alkemy.security.services;

import com.alkemy.security.entities.Role;
import com.alkemy.security.enums.RoleName;

import java.util.Optional;

public interface IRoleService {

    Optional<Role> getByRoleName(RoleName roleName);
}
