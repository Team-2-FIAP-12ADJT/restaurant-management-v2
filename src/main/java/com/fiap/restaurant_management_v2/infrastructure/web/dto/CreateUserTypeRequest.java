package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserTypeRequest(
    @NotBlank(message = "Tipo de usuário é obrigatório") String userType
) {}
