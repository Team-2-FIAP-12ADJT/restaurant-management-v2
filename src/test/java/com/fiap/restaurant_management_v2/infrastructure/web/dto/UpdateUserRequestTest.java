package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateUserRequestTest {

    private static UpdateUserRequest withTax(String taxIdentifier) {
        return new UpdateUserRequest(
            "Ada",
            "ada@example.com",
            "ada",
            taxIdentifier
        );
    }

    @Test
    @DisplayName("CPF ausente (null) permanece null — campo não alterado no PATCH")
    void nullTaxStaysNull() {
        assertNull(withTax(null).taxIdentifier());
    }

    @Test
    @DisplayName("Normaliza CPF mascarado para 11 dígitos crus")
    void normalizesMaskedCpf() {
        assertEquals("12345678901", withTax("123.456.789-01").taxIdentifier());
    }

    @Test
    @DisplayName(
        "Construtor NÃO lança para CPF inválido — só normaliza. " +
        "Rejeição de formato é Bean Validation (@Pattern)."
    )
    void invalidCpfDoesNotThrowOnlyNormalizes() {
        assertEquals("123", withTax("12.3").taxIdentifier());
    }
}
