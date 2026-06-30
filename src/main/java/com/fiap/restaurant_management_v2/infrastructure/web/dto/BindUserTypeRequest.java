package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BindUserTypeRequest(
        @NotNull(message = "O ID do usuário é obrigatório") UUID userId,
        @NotNull(message = "O ID do tipo é obrigatório") UUID typeId

) {}
