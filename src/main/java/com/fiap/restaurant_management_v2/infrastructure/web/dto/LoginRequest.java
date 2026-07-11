package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Login is required") @Schema(example = "admin") String login,
    @NotBlank(message = "Password is required") @Schema(example = "Senh@1234") String password
) {
    public LoginRequest {
        login = login != null ? login.trim().toLowerCase() : null;
        password = password != null ? password.trim() : null;
    }
}
