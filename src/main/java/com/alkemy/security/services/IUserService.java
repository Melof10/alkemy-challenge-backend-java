package com.alkemy.security.services;

import com.alkemy.security.entities.User;

import java.util.Optional;

public interface IUserService {

    Optional<User> getByUsername(String username);

    Optional<User> getByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void save(User user);
}
