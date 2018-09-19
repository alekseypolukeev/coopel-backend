package com.coopel.auth.repository;

import com.coopel.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findOneByName(String name);
}
