package io.github.zlemiesz.springemployeeservice.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Zbigniew Lemiesz
 */
public class EmployeePutDto {

    @NotBlank(message = "{common.notBlank}")
    private String firstName;

    @NotBlank(message = "{common.notBlank}")
    private String lastName;

    @NotBlank(message = "{common.notBlank}")
    @Email(message = "{common.email}")
    private String email;

    @NotNull(message = "{common.version}")
    private Long version;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
