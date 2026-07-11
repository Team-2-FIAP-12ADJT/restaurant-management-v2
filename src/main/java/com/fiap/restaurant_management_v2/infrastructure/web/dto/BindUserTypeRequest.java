package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BindUserTypeRequest(
        @NotNull(message = "O ID do usuário é obrigatório") @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID userId,
        @NotNull(message = "O ID do tipo é obrigatório") @Schema(example = "5c2d1f90-1111-4222-8333-444455556666") UUID typeId

) {}
