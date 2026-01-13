package io.github.zlemiesz.springemployeeservice.exception;

/**
 * @author Zbigniew Lemiesz
 */
public class EmailAlreadyInUseException extends RuntimeException {
    private final String email;

    public EmailAlreadyInUseException(String email) {
        super("Email already in use: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}