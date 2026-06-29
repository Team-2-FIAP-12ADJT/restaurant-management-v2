package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
    String name,
    @Email String email,
    String login,
    String taxIdentifier
) {
    private static final String EMAIL_REGEX =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final String TAX_IDENTIFIER_REGEX = "^\\d{11}$";

    public UpdateUserRequest {
        if (email != null && !email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Partial (PATCH): null = campo ausente (mantém atual). Campo presente é
        // normalizado igual ao create; blank vira "" (não null) p/ cair na
        // validação do domínio (400) em vez de virar no-op silencioso.
        name = name != null ? name.trim() : null;
        email = email != null ? email.trim().toLowerCase() : null;
        login = login != null ? login.trim().toLowerCase() : null;

        // CPF aceito com ou sem máscara; normaliza para 11 dígitos crus.
        if (taxIdentifier != null) {
            taxIdentifier = taxIdentifier.replaceAll("\\D", "");
            if (!taxIdentifier.matches(TAX_IDENTIFIER_REGEX)) {
                throw new IllegalArgumentException(
                    "Tax identifier must be a valid CPF (11 digits)"
                );
            }
        }
    }
}
