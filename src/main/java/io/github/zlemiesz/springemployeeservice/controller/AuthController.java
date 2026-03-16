package io.github.zlemiesz.springemployeeservice.controller;

import io.github.zlemiesz.springemployeeservice.dto.auth.AuthMeResponse;
import io.github.zlemiesz.springemployeeservice.dto.auth.ChangePasswordRequest;
import io.github.zlemiesz.springemployeeservice.security.UserPrincipal;
import io.github.zlemiesz.springemployeeservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zbigniew Lemiesz
 */


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal UserPrincipal principal,
                               @Valid @RequestBody ChangePasswordRequest req){
        authService.changePassword(principal.getEmail(), req);
    }
}
