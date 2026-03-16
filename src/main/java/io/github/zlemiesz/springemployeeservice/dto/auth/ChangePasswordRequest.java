package io.github.zlemiesz.springemployeeservice.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author Zbigniew Lemiesz
 */
public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,
        @NotBlank @Size(min = 8, max = 36, message = "New password must be at least 8 characters")
        String newPassword
) {
}

