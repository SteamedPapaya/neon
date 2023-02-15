package com.mouken.modules.role.repository;

import com.mouken.modules.role.entity.RoleHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {

    RoleHierarchy findByRoleName(String roleName);
}