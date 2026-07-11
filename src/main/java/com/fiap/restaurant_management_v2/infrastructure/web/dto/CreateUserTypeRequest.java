package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateUserTypeRequest(
    @NotBlank(message = "Tipo de usuário é obrigatório") @Schema(example = "DONO") String userType
) {}
