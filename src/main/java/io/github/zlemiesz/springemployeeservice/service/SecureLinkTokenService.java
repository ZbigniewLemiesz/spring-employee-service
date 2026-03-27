package io.github.zlemiesz.springemployeeservice.service;

/**
 * @author Zbigniew Lemiesz
 */

public interface SecureLinkTokenService {
    String generateRawToken();

    String hash(String rawToken);
}
