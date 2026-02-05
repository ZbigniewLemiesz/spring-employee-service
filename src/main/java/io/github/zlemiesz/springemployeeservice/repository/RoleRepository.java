package io.github.zlemiesz.springemployeeservice.repository;

import io.github.zlemiesz.springemployeeservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Zbigniew Lemiesz
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
