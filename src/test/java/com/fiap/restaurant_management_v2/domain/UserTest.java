package com.fiap.restaurant_management_v2.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fiap.restaurant_management_v2.domain.exception.InvalidUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UserTest {

    private static final String VALID_CPF = "12345678901";

    @Test
    @DisplayName("Cria usuário com sucesso e gera id")
    void createsUserSuccessfully() {
        User user = User.create(
            "Ada Lovelace",
            "ada@example.com",
            "ada",
            VALID_CPF,
            "secret123"
        );

        assertNotNull(user.getId());
        assertEquals("Ada Lovelace", user.getName());
        assertEquals("ada@example.com", user.getEmail());
        assertEquals("ada", user.getLogin());
        assertEquals(VALID_CPF, user.getTaxIdentifier());
        assertEquals("secret123", user.getPassword());
    }

    @Test
    @DisplayName("Proíbe nome em branco")
    void rejectsBlankName() {
        assertThrows(InvalidUserException.class, () ->
            User.create("  ", "ada@example.com", "ada", VALID_CPF, "secret123")
        );
    }

    @Test
    @DisplayName("Proíbe login em branco")
    void rejectsBlankLogin() {
        assertThrows(InvalidUserException.class, () ->
            User.create("Ada", "ada@example.com", "", VALID_CPF, "secret123")
        );
    }

    @Test
    @DisplayName("Proíbe senha em branco")
    void rejectsBlankPassword() {
        assertThrows(InvalidUserException.class, () ->
            User.create("Ada", "ada@example.com", "ada", VALID_CPF, "")
        );
    }

    @Test
    @DisplayName("Proíbe email inválido")
    void rejectsInvalidEmail() {
        assertThrows(InvalidUserException.class, () ->
            User.create("Ada", "not-an-email", "ada", VALID_CPF, "secret123")
        );
    }

    @Test
    @DisplayName("Aceita CPF com 11 dígitos crus")
    void acceptsValidTaxIdentifier() {
        User user = User.create(
            "Ada",
            "ada@example.com",
            "ada",
            VALID_CPF,
            "secret123"
        );

        assertEquals(VALID_CPF, user.getTaxIdentifier());
    }

    @ParameterizedTest
    @DisplayName("Proíbe CPF fora do formato canônico (11 dígitos crus)")
    @ValueSource(
        strings = {
            "123456789",        // curto demais
            "123456789012",     // longo demais
            "1234567890a",      // contém letra
            "123.456.789-01",   // mascarado (domínio exige dígitos crus)
            " ",                // em branco
        }
    )
    void rejectsInvalidTaxIdentifier(String taxIdentifier) {
        assertThrows(InvalidUserException.class, () ->
            User.create(
                "Ada",
                "ada@example.com",
                "ada",
                taxIdentifier,
                "secret123"
            )
        );
    }

    @Test
    @DisplayName("Proíbe CPF nulo")
    void rejectsNullTaxIdentifier() {
        assertThrows(InvalidUserException.class, () ->
            User.create("Ada", "ada@example.com", "ada", null, "secret123")
        );
    }

    @Test
    @DisplayName("Rehidrata sem regenerar id nem validar")
    void restoresWithoutRegeneratingId() {
        var id = java.util.UUID.randomUUID();
        User user = User.restore(
            id,
            "Ada",
            "ada@example.com",
            "ada",
            VALID_CPF,
            "hash"
        );

        assertEquals(id, user.getId());
    }
}
