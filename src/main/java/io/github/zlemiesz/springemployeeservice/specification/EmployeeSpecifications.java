package io.github.zlemiesz.springemployeeservice.specification;

import io.github.zlemiesz.springemployeeservice.model.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zbigniew Lemiesz
 */



public class EmployeeSpecifications {

    public static Specification<Employee> filter(
            String firstName,
            String lastName,
            String email
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (firstName != null && !firstName.isEmpty()) {
                predicates.add(
                        cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%")
                );
            }

            if (lastName != null && !lastName.isEmpty()) {
                predicates.add(
                        cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%")
                );
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(
                        cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%")
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

