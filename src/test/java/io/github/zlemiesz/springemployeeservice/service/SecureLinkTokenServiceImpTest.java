package io.github.zlemiesz.springemployeeservice.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Zbigniew Lemiesz
 */
public class SecureLinkTokenServiceImpTest {
    private final SecureLinkTokenService service = new SecureLinkTokenServiceImp();

    @Test
    void shouldGenerateDifferentRawTokens() {
        String token1 = service.generateRawToken();
        String token2 = service.generateRawToken();

        assertThat(token1).isNotBlank();
        assertThat(token2).isNotBlank();
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldHashTokenDeterministically(){
        String rawToken = "test-token";

        String hash1 = service.hash(rawToken);
        String hash2 = service.hash(rawToken);

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(rawToken);
    }

}
