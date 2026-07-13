package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTypeRequestDtosTest {

    @Test
    @DisplayName("CreateUserTypeRequest trim espacos")
    void createUserTypeRequestTrims() {
        var dto = new CreateUserTypeRequest("  Dono  ");
        assertEquals("Dono", dto.userType());
    }

    @Test
    @DisplayName("CreateUserTypeRequest null safe")
    void createUserTypeRequestNullSafe() {
        var dto = new CreateUserTypeRequest(null);
        assertNull(dto.userType());
    }

    @Test
    @DisplayName("UpdateUserTypeRequest trim espacos")
    void updateUserTypeRequestTrims() {
        var dto = new UpdateUserTypeRequest("  Cliente  ");
        assertEquals("Cliente", dto.userType());
    }

    @Test
    @DisplayName("UpdateUserTypeRequest null safe")
    void updateUserTypeRequestNullSafe() {
        var dto = new UpdateUserTypeRequest(null);
        assertNull(dto.userType());
    }
}
