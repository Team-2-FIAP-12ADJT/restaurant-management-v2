package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateUserRequestTest {

    private static CreateUserRequest withTax(String taxIdentifier) {
        return new CreateUserRequest(
            "Ada",
            "ada@example.com",
            "ada",
            taxIdentifier,
            "secret123"
        );
    }

    @Test
    @DisplayName("Normaliza CPF mascarado para 11 dígitos crus")
    void normalizesMaskedCpf() {
        assertEquals("12345678901", withTax("123.456.789-01").taxIdentifier());
    }

    @Test
    @DisplayName("Aceita CPF já em dígitos crus")
    void acceptsRawCpf() {
        assertEquals("12345678901", withTax("12345678901").taxIdentifier());
    }

    @Test
    @DisplayName(
        "Construtor NÃO lança para CPF inválido — só normaliza (tira não-dígitos). " +
        "Rejeição de formato é Bean Validation (@Pattern), testada em UserApiIT."
    )
    void invalidCpfDoesNotThrowOnlyNormalizes() {
        assertEquals("123", withTax("12.3").taxIdentifier());
        assertEquals("1234567890", withTax("1234567890").taxIdentifier());
    }
}
