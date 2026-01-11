package io.github.zlemiesz.springemployeeservice.employee;

import io.github.zlemiesz.springemployeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Zbigniew Lemiesz
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
     Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String newEmail);
}
