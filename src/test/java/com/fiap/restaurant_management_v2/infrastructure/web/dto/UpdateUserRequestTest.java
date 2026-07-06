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

    private static UpdateUserRequest build(String name, String email, String login, String taxIdentifier) {
        return new UpdateUserRequest(name, email, login, taxIdentifier);
    }

    // --- CAMPO CPF ---
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

    // --- CAMPO NOME (remoção de espaços) ---
    @Test
    @DisplayName("name: null stays null (PATCH — omitted field)")
    void nameNullStaysNull() {
        assertNull(build(null, "ada@example.com", "ada", "12345678901").name());
    }

    @Test
    @DisplayName("name: trims whitespace")
    void nameTrimmed() {
        assertEquals("Ada", build("  Ada  ", "ada@example.com", "ada", "12345678901").name());
    }

    // --- CAMPO EMAIL (remoção de espaços + minúsculas) ---
    @Test
    @DisplayName("email: null stays null (PATCH — omitted field)")
    void emailNullStaysNull() {
        assertNull(build("Ada", null, "ada", "12345678901").email());
    }

    @Test
    @DisplayName("email: trims and converts to lowercase")
    void emailTrimmedAndLowercased() {
        assertEquals(
            "ada@example.com",
            build("Ada", "  ADA@EXAMPLE.COM  ", "ada", "12345678901").email()
        );
    }

    // --- CAMPO LOGIN (remoção de espaços + minúsculas) ---
    @Test
    @DisplayName("login: null stays null (PATCH — omitted field)")
    void loginNullStaysNull() {
        assertNull(build("Ada", "ada@example.com", null, "12345678901").login());
    }

    @Test
    @DisplayName("login: trims and converts to lowercase")
    void loginTrimmedAndLowercased() {
        assertEquals(
            "ada",
            build("Ada", "ada@example.com", "  ADA  ", "12345678901").login()
        );
    }

    // --- TESTES DE COMBINAÇÃO ---
    @Test
    @DisplayName("All fields null together (full PATCH omission)")
    void allFieldsNullTogether() {
        var req = build(null, null, null, null);
        assertNull(req.name());
        assertNull(req.email());
        assertNull(req.login());
        assertNull(req.taxIdentifier());
    }

    @Test
    @DisplayName("All fields normalized simultaneously")
    void allFieldsNormalizedSimultaneously() {
        var req = build(
            "  Ada  ",
            "  ADA@EXAMPLE.COM  ",
            "  ADA  ",
            "123.456.789-01"
        );
        assertEquals("Ada", req.name());
        assertEquals("ada@example.com", req.email());
        assertEquals("ada", req.login());
        assertEquals("12345678901", req.taxIdentifier());
    }
}
