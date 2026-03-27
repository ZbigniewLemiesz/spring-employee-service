package io.github.zlemiesz.springemployeeservice.exception;

/**
 * @author Zbigniew Lemiesz
 */
public class UserAccountAlreadyExistsException extends RuntimeException {
    public UserAccountAlreadyExistsException(Long employeeId) {
        super("UserAccount already exists for employeeId=" + employeeId);
    }
}
