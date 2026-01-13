package io.github.zlemiesz.springemployeeservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Zbigniew Lemiesz
 */
public class EmployeeCreateDto {

    @NotBlank(message = "{common.notBlank}")
    private String firstName;

    @NotBlank(message = "{common.notBlank}")
    private String lastName;

    @NotBlank(message = "{common.notBlank}")
    @Email(message = "{common.email}")
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
