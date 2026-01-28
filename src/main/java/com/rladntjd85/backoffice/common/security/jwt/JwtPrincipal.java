package com.rladntjd85.backoffice.common.security.jwt;

public record JwtPrincipal(Long userId, String email, String role) { }
