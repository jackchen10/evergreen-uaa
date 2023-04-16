package org.evergreen.evergreenuaa.repository;

import org.evergreen.evergreenuaa.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findOptionalByAuthority(String authority);

}
