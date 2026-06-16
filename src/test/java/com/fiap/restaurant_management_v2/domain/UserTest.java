package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {
    @Test
    @DisplayName("Cria usuário com sucesso e gera id")
    void createsUserSuccessfully() {
        User user = User.create("Ada Lovelace", "ada@example.com", "ada", "secret123");

        assertNotNull(user.getId());
        assertEquals("Ada Lovelace", user.getName());
        assertEquals("ada@example.com", user.getEmail());
        assertEquals("ada", user.getLogin());
        assertEquals("secret123", user.getPassword());
    }

    @Test
    @DisplayName("Proíbe nome em branco")
    void rejectsBlankName() {
        assertThrows(InvalidUserException.class,
                () -> User.create("  ", "ada@example.com", "ada", "secret123"));
    }

    @Test
    @DisplayName("Proíbe login em branco")
    void rejectsBlankLogin() {
        assertThrows(InvalidUserException.class,
                () -> User.create("Ada", "ada@example.com", "", "secret123"));
    }

    @Test
    @DisplayName("Proíbe senha em branco")
    void rejectsBlankPassword() {
        assertThrows(InvalidUserException.class,
                () -> User.create("Ada", "ada@example.com", "ada", null));
    }

    @Test
    @DisplayName("Proíbe email inválido")
    void rejectsInvalidEmail() {
        assertThrows(InvalidUserException.class,
                () -> User.create("Ada", "not-an-email", "ada", "secret123"));
    }

    @Test
    @DisplayName("Rehidrata sem regenerar id nem validar")
    void restoresWithoutRegeneratingId() {
        var id = java.util.UUID.randomUUID();
        User user = User.restore(id, "Ada", "ada@example.com", "ada", "hash");

        assertEquals(id, user.getId());
    }
}
