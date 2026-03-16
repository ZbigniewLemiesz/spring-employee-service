package io.github.zlemiesz.springemployeeservice.service;

import io.github.zlemiesz.springemployeeservice.dto.auth.ChangePasswordRequest;
import io.github.zlemiesz.springemployeeservice.model.UserAccount;
import io.github.zlemiesz.springemployeeservice.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Zbigniew Lemiesz
 */
@Service
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void changePassword(String userEmail, ChangePasswordRequest req) {
        UserAccount userAccount = userAccountRepository.findForLoginByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Account not found"));

        if (!passwordEncoder.matches(req.currentPassword(), userAccount.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid current password");
        }

        if (passwordEncoder.matches(req.newPassword(), userAccount.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different");
        }

        userAccount.setPasswordHash(passwordEncoder.encode(req.newPassword()));
    }
}
