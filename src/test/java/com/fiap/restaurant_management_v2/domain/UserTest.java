package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("validateDetails proibe email nulo")
    void validateDetailsRejectsNullEmail() {
        assertThrows(InvalidUserException.class, () ->
            User.validateDetails("Ada", null, "ada", VALID_CPF)
        );
    }

    @Test
    @DisplayName("Bind mantem id e senha informados")
    void bindPreservesValues() {
        var id = java.util.UUID.randomUUID();

        User user = User.bind(
            id,
            "Ada",
            "ada@example.com",
            "ada",
            VALID_CPF,
            "hash"
        );

        assertEquals(id, user.getId());
        assertEquals("hash", user.getPassword());
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

    @Test
    @DisplayName("equals: two users with same id are equal")
    void equalsWithSameId() {
        var id = java.util.UUID.randomUUID();
        User user1 = User.restore(id, "Ada", "ada@example.com", "ada", VALID_CPF, "hash");
        User user2 = User.restore(id, "Bob", "bob@example.com", "bob", "12345678902", "different");

        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("equals: same instance returns true")
    void equalsWithSameInstance() {
        User user = User.create("Ada", "ada@example.com", "ada", VALID_CPF, "secret123");
        assertEquals(user, user);
    }

    @Test
    @DisplayName("equals: different ids are not equal")
    void equalsWithDifferentIds() {
        User user1 = User.create("Ada", "ada@example.com", "ada", VALID_CPF, "secret123");
        User user2 = User.create("Bob", "bob@example.com", "bob", VALID_CPF, "secret123");

        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("equals: not equal to object of different type")
    void equalsWithDifferentType() {
        User user = User.create("Ada", "ada@example.com", "ada", VALID_CPF, "secret123");
        assertNotEquals(user, "not a user");
    }

    @Test
    @DisplayName("equals: id diferente retorna false")
    void equalsMethodReturnsFalseForDifferentIds() {
        User user1 = User.restore(java.util.UUID.randomUUID(), "Ada", "ada@example.com", "ada", VALID_CPF, "hash");
        User user2 = User.restore(java.util.UUID.randomUUID(), "Ada", "ada@example.com", "ada", VALID_CPF, "hash");

        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("equals: not equal to null")
    void equalsWithNull() {
        User user = User.create("Ada", "ada@example.com", "ada", VALID_CPF, "secret123");
        assertNotEquals(null, user);
    }

    @Test
    @DisplayName("hashCode: same id produces same hash")
    void hashCodeConsistentWithId() {
        var id = java.util.UUID.randomUUID();
        User user1 = User.restore(id, "Ada", "ada@example.com", "ada", VALID_CPF, "hash");
        User user2 = User.restore(id, "Bob", "bob@example.com", "bob", "12345678902", "different");

        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
