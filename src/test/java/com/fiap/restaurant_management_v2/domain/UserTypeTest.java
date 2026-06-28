package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidUserTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTypeTest {

    @Test
    @DisplayName("Cria UserType com ID gerado e nome válido")
    void createsValidUserType() {
        var userType = UserType.create("admin");

        assertNotNull(userType.getId());
        assertEquals("admin", userType.getUserType());
    }

    @Test
    @DisplayName("Nome vazio lança InvalidUserTypeException")
    void rejectsEmptyName() {
        assertThrows(InvalidUserTypeException.class, () -> UserType.create(""));
        assertThrows(InvalidUserTypeException.class, () -> UserType.create("   "));
        assertThrows(InvalidUserTypeException.class, () -> UserType.create(null));
    }

    @Test
    @DisplayName("Restore mantém ID e nome fornecidos")
    void restorePreservesValues() {
        var id = UUID.randomUUID();
        var userType = UserType.restore(id, "admin");

        assertEquals(id, userType.getId());
        assertEquals("admin", userType.getUserType());
    }

    @Test
    @DisplayName("Restore com ID nulo lança NullPointerException")
    void restoreRejectsNullId() {
        assertThrows(NullPointerException.class, () -> UserType.restore(null, "admin"));
    }

    @Test
    @DisplayName("changeType altera o nome mantendo o ID")
    void changeTypePreservesId() {
        var userType = UserType.create("admin");
        var updated = userType.changeType("manager");

        assertEquals(userType.getId(), updated.getId());
        assertEquals("manager", updated.getUserType());
    }

    @Test
    @DisplayName("changeType com nome vazio lança InvalidUserTypeException")
    void changeTypeRejectsEmpty() {
        var userType = UserType.create("admin");
        assertThrows(InvalidUserTypeException.class, () -> userType.changeType(""));
        assertThrows(InvalidUserTypeException.class, () -> userType.changeType(null));
    }

    @Test
    @DisplayName("equals e hashCode baseados no ID")
    void equalsAndHashCode() {
        var id = UUID.randomUUID();
        var type1 = UserType.restore(id, "admin");
        var type2 = UserType.restore(id, "admin");
        var type3 = UserType.restore(UUID.randomUUID(), "waiter");

        assertEquals(type1, type2);
        assertNotEquals(type1, type3);
        assertEquals(type1.hashCode(), type2.hashCode());
    }
}
