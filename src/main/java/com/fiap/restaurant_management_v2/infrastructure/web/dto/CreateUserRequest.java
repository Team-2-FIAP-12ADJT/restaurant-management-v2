package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Email is required") @Email String email,
    @NotBlank(message = "Login is required") String login,
    @NotBlank()
    @Pattern(
        regexp = "^\\d{11}$",
        message = "Tax identifier must be a valid CPF (11 digits)"
    )
    String taxIdentifier,
    @NotBlank(message = "Password is required")
    @Size(
        min = 6,
        max = 12,
        message = "Password must be between 6 and 12 characters long"
    )
    String password
) {
    /**
     * O construtor SÓ normaliza — nunca lança. Validação estrutural/formato fica
     * nas anotações Bean Validation (acima), para que campo ausente/ inválido vire
     * {@code MethodArgumentNotValidException} (erro por campo), e não um
     * {@code HttpMessageNotReadableException} opaco durante a desserialização.
     * CPF é normalizado para 11 dígitos crus antes do {@code @Pattern} validar.
     */
    public CreateUserRequest {
        name = name != null ? name.trim() : null;
        email = email != null ? email.trim().toLowerCase() : null;
        login = login != null ? login.trim().toLowerCase() : null;
        password = password != null ? password.trim() : null;
        taxIdentifier =
            taxIdentifier != null ? taxIdentifier.replaceAll("\\D", "") : null;
    }
}
