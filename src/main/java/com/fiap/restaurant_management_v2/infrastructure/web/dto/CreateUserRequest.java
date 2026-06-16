package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank String login,
    @NotBlank String password
) {}
