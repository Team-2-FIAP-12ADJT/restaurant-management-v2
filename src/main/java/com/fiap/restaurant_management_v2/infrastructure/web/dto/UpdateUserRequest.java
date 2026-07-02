package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequest(
    String name,
    @Email String email,
    String login,
    @Pattern(
        regexp = "^\\d{11}$",
        message = "Tax identifier must be a valid CPF (11 digits)"
    )
    String taxIdentifier
) {
    /**
     * PATCH parcial: campo ausente (null) = mantém o atual. O construtor SÓ
     * normaliza (sem lançar) — formato vai para {@code @Pattern}, que ignora null
     * (campo ausente) e valida o presente. CPF normalizado para 11 dígitos crus
     * antes da validação.
     */
    public UpdateUserRequest {
        name = name != null ? name.trim() : null;
        email = email != null ? email.trim().toLowerCase() : null;
        login = login != null ? login.trim().toLowerCase() : null;
        taxIdentifier = taxIdentifier != null
            ? taxIdentifier.replaceAll("\\D", "")
            : null;
    }
}
