package org.evergreen.evergreenuaa.repository;

import org.evergreen.evergreenuaa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOptionalByUsername(String username);
}
