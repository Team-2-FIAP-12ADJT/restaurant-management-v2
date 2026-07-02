package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CpfFormatterTest {

    @Test
    @DisplayName("Formata 11 dígitos crus para a máscara XXX.XXX.XXX-XX")
    void formatsRawDigits() {
        assertEquals("123.456.789-01", CpfFormatter.format("12345678901"));
    }

    @Test
    @DisplayName("Retorna null inalterado")
    void returnsNullAsIs() {
        assertNull(CpfFormatter.format(null));
    }

    @Test
    @DisplayName("Retorna valor não-canônico inalterado (defensivo)")
    void returnsNonCanonicalAsIs() {
        assertEquals("123", CpfFormatter.format("123"));
        assertEquals("123.456.789-01", CpfFormatter.format("123.456.789-01"));
    }
}
