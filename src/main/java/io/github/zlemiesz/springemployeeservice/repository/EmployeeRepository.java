package io.github.zlemiesz.springemployeeservice.repository;

import io.github.zlemiesz.springemployeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @author Zbigniew Lemiesz
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String newEmail);
}
