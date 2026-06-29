package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @DisplayName("Rejeita CPF inválido com 400 (IllegalArgumentException)")
    @ValueSource(strings = { "123", "123456789012", "1234567890a", "abc" })
    void rejectsInvalidCpf(String taxIdentifier) {
        assertThrows(IllegalArgumentException.class, () ->
            withTax(taxIdentifier)
        );
    }
}
