package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CnpjFormatterTest {

    @Test
    @DisplayName("Formata 14 chars crus para a máscara XX.XXX.XXX/XXXX-XX")
    void formatsRawCnpj() {
        assertEquals("59.747.119/0001-75", CnpjFormatter.format("59747119000175"));
    }

    @Test
    @DisplayName("Retorna null inalterado")
    void returnsNullAsIs() {
        assertNull(CnpjFormatter.format(null));
    }

    @Test
    @DisplayName("Retorna valor não-canônico inalterado (defensivo)")
    void returnsNonCanonicalAsIs() {
        assertEquals("123", CnpjFormatter.format("123"));
        assertEquals(
            "59.747.119/0001-75",
            CnpjFormatter.format("59.747.119/0001-75")
        );
    }
}
