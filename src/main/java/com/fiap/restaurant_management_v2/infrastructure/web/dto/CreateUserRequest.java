package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Email is required") @Email String email,
    @NotBlank(message = "Login is required") String login,
    @NotBlank(message = "Tax identifier is required") String taxIdentifier,
    @NotBlank(message = "Password is required")
    @Size(
        min = 6,
        max = 12,
        message = "Password must be between 6 and 12 characters long"
    )
    String password
) {
    private static final String EMAIL_REGEX =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final String TAX_IDENTIFIER_REGEX = "^\\d{11}$";

    public CreateUserRequest {
        if (email != null && !email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // CPF aceito com ou sem máscara; normaliza para 11 dígitos crus (formato
        // canônico de armazenamento). Validação roda sobre o valor normalizado.
        if (taxIdentifier != null) {
            taxIdentifier = taxIdentifier.replaceAll("\\D", "");
            if (!taxIdentifier.matches(TAX_IDENTIFIER_REGEX)) {
                throw new IllegalArgumentException(
                    "Tax identifier must be a valid CPF (11 digits)"
                );
            }
        }

        login = login != null ? login.trim().toLowerCase() : null;
        name = name != null ? name.trim() : null;
        password = password != null ? password.trim() : null;
        email = email != null ? email.trim().toLowerCase() : null;
    }
}
