package io.github.zlemiesz.springemployeeservice.exception;

/**
 * @author Zbigniew Lemiesz
 */
public class EmployeeNotFoundException extends RuntimeException {

    private final Long id;

    public EmployeeNotFoundException(Long id) {
        super("No employee with id: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
