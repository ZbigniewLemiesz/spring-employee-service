package io.github.zlemiesz.springemployeeservice.repository;

import io.github.zlemiesz.springemployeeservice.model.PasswordSetupToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Zbigniew Lemiesz
 */

public interface PasswordSetupTokenRepository  extends JpaRepository<PasswordSetupToken, Long> {
    Optional<PasswordSetupToken> findByTokenHash(String tokenHash);
    void deleteAllByUserAccount_id(Long userAccountId);
    void deleteAllByUserAccount_IdAndUsedAtIsNull(Long userAccountId);
}
