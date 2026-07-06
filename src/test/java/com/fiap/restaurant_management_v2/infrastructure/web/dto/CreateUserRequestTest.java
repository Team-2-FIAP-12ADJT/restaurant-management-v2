package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    private static CreateUserRequest build(
        String name,
        String email,
        String login,
        String password
    ) {
        return new CreateUserRequest(name, email, login, "12345678901", password);
    }

    // --- CAMPO IDENTIFICADOR FISCAL (CPF) ---
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

    // --- CAMPO NOME (remoção de espaços) ---
    @Test
    @DisplayName("name: trims leading/trailing whitespace")
    void nameTrimmed() {
        assertEquals("Ada", build("  Ada  ", "ada@example.com", "ada", "secret123").name());
    }

    @Test
    @DisplayName("name: null stays null when explicitly provided")
    void nameNullStaysNull() {
        // Observação: na prática, @NotBlank impede null, mas o construtor deve tratar isso.
        var req = new CreateUserRequest(null, "ada@example.com", "ada", "12345678901", "secret123");
        assertNull(req.name());
    }

    // --- CAMPO EMAIL (remoção de espaços + minúsculas) ---
    @Test
    @DisplayName("email: trims and converts to lowercase")
    void emailTrimmedAndLowercased() {
        assertEquals(
            "ada@example.com",
            build("Ada", "  ADA@EXAMPLE.COM  ", "ada", "secret123").email()
        );
    }

    @Test
    @DisplayName("email: null stays null")
    void emailNullStaysNull() {
        var req = new CreateUserRequest("Ada", null, "ada", "12345678901", "secret123");
        assertNull(req.email());
    }

    // --- CAMPO LOGIN (remoção de espaços + minúsculas) ---
    @Test
    @DisplayName("login: trims and converts to lowercase")
    void loginTrimmedAndLowercased() {
        assertEquals(
            "ada",
            build("Ada", "ada@example.com", "  ADA  ", "secret123").login()
        );
    }

    @Test
    @DisplayName("login: null stays null")
    void loginNullStaysNull() {
        var req = new CreateUserRequest("Ada", "ada@example.com", null, "12345678901", "secret123");
        assertNull(req.login());
    }

    // --- CAMPO SENHA (remoção de espaços) ---
    @Test
    @DisplayName("password: trims leading/trailing whitespace")
    void passwordTrimmed() {
        assertEquals(
            "secret123",
            build("Ada", "ada@example.com", "ada", "  secret123  ").password()
        );
    }

    @Test
    @DisplayName("password: null stays null")
    void passwordNullStaysNull() {
        var req = new CreateUserRequest("Ada", "ada@example.com", "ada", "12345678901", null);
        assertNull(req.password());
    }

    // --- TESTES DE COMBINAÇÃO ---
    @Test
    @DisplayName("All fields normalized simultaneously")
    void allFieldsNormalizedTogether() {
        var req = new CreateUserRequest(
            "  Ada  ",
            "  ADA@EXAMPLE.COM  ",
            "  ADA  ",
            "123.456.789-01",
            "  secret123  "
        );
        assertEquals("Ada", req.name());
        assertEquals("ada@example.com", req.email());
        assertEquals("ada", req.login());
        assertEquals("12345678901", req.taxIdentifier());
        assertEquals("secret123", req.password());
    }
}
