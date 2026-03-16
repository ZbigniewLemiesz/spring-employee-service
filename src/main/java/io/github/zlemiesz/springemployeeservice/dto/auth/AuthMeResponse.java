package io.github.zlemiesz.springemployeeservice.dto.auth;


import java.util.Set;

public record AuthMeResponse(
        Long userAccountId,
        String email,
        Set<String> roles
) {}
