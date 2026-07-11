package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Login is required") String login,
    @NotBlank(message = "Password is required") String password
) {
    public LoginRequest {
        login = login != null ? login.trim().toLowerCase() : null;
        password = password != null ? password.trim() : null;
    }
}
