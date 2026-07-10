package com.fiap.restaurant_management_v2.infrastructure.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SensitiveDataMaskerTest {

    private final SensitiveDataMasker masker = new SensitiveDataMasker();

    @Test
    @DisplayName("Mascara password e taxIdentifier no nível raiz")
    void masksTopLevelSensitiveFields() {
        String masked = masker.mask(
            "{\"login\":\"john\",\"password\":\"s3cret\",\"taxIdentifier\":\"12345678901\"}"
        );

        assertFalse(masked.contains("s3cret"));
        assertFalse(masked.contains("12345678901"));
        assertTrue(masked.contains("\"password\":\"***\""));
        assertTrue(masked.contains("\"taxIdentifier\":\"***\""));
        assertTrue(masked.contains("\"login\":\"john\""));
    }

    @Test
    @DisplayName("Mascara campos sensíveis aninhados em objetos e arrays")
    void masksNestedFields() {
        String masked = masker.mask(
            "{\"owner\":{\"taxIdentifier\":\"98765432100\"},"
                + "\"users\":[{\"password\":\"abc\"},{\"password\":\"def\"}]}"
        );

        assertFalse(masked.contains("98765432100"));
        assertFalse(masked.contains("abc"));
        assertFalse(masked.contains("def"));
    }

    @Test
    @DisplayName("Body não-JSON nunca vaza cru")
    void neverLeaksUnparseableBody() {
        assertEquals(
            "[unparseable body]",
            masker.mask("password=s3cret&login=john")
        );
    }

    @Test
    @DisplayName("Body nulo ou em branco vira string vazia")
    void blankBodyBecomesEmpty() {
        assertEquals("", masker.mask(null));
        assertEquals("", masker.mask("  "));
    }

    @Test
    @DisplayName("Mascara campo sensível independente de caixa (Password, TAXIDENTIFIER)")
    void masksCaseInsensitively() {
        String masked = masker.mask(
            "{\"Password\":\"s3cret\",\"TAXIDENTIFIER\":\"12345678901\"}"
        );

        assertFalse(masked.contains("s3cret"));
        assertFalse(masked.contains("12345678901"));

        assertEquals(
            "TaxIdentifier=***&page=1",
            masker.maskQueryString("TaxIdentifier=12345678901&page=1")
        );
    }

    @Test
    @DisplayName("Query string: mascara valor de param sensível, preserva o resto")
    void masksSensitiveQueryParams() {
        assertEquals(
            "taxIdentifier=***&name=Ada&page=1",
            masker.maskQueryString("taxIdentifier=12345678901&name=Ada&page=1")
        );
    }

    @Test
    @DisplayName("Query string sem param sensível ou nula passa intacta")
    void queryWithoutSensitiveParamsUnchanged() {
        assertEquals("name=Ada&page=2", masker.maskQueryString("name=Ada&page=2"));
        assertEquals("flagOnly", masker.maskQueryString("flagOnly"));
        assertNull(masker.maskQueryString(null));
    }
}
