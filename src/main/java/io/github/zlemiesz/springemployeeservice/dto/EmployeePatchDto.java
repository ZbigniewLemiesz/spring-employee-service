package io.github.zlemiesz.springemployeeservice.employee;

import io.github.zlemiesz.springemployeeservice.validation.NullOrNotBlank;
import jakarta.validation.constraints.*;

/**
 * @author Zbigniew Lemiesz
 */
public class EmployeePatchDto {

    @NullOrNotBlank(message = "{common.notBlank}")
    private String firstName;

    @NullOrNotBlank(message = "{common.notBlank}")
    private String lastName;

    @Email(message = "{common.email}")
    @NullOrNotBlank(message = "{common.notBlank}")
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
