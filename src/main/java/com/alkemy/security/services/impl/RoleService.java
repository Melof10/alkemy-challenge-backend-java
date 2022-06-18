package com.alkemy.security.services.impl;

import com.alkemy.security.entities.Role;
import com.alkemy.security.enums.RoleName;
import com.alkemy.security.repositories.RoleRepository;
import com.alkemy.security.services.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RoleService implements IRoleService {

    @Autowired
    RoleRepository roleRepository;

    public Optional<Role> getByRoleName(RoleName roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
