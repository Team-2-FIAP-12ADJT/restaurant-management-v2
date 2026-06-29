package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UpdateUserRequestTest {

    private static UpdateUserRequest withTax(String taxIdentifier) {
        return new UpdateUserRequest("Ada", "ada@example.com", "ada", taxIdentifier);
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

    @ParameterizedTest
    @DisplayName("Rejeita CPF presente inválido (inclui blank) com 400")
    @ValueSource(strings = { "123", "123456789012", "1234567890a", " " })
    void rejectsInvalidCpf(String taxIdentifier) {
        assertThrows(IllegalArgumentException.class, () ->
            withTax(taxIdentifier)
        );
    }
}
