package io.github.zlemiesz.springemployeeservice.controller;

import io.github.zlemiesz.springemployeeservice.dto.common.AuthMeResponse;
import io.github.zlemiesz.springemployeeservice.security.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zbigniew Lemiesz
 */


@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/me")
    public AuthMeResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        Set<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // np. ROLE_ADMIN
                .collect(Collectors.toSet());

        return new AuthMeResponse(
                principal.getUserAccountId(),
                principal.getEmail(),
                roles
        );
    }
}
