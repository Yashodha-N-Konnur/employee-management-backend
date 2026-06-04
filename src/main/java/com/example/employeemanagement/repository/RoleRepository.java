package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access layer for {@link Role}.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByRoleName(String roleName);

    boolean existsByRoleNameAndIdNot(String roleName, Long id);

    Optional<Role> findByRoleName(String roleName);

    List<Role> findByRoleNameContainingIgnoreCase(String name);
}
