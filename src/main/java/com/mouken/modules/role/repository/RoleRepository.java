package com.mouken.modules.role.repository;

import com.mouken.modules.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(String name);
    Role findByName(String name);
}
