package com.fiap.restaurant_management_v2.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionsTest {

    @Test
    void invalidUserTypeUuidException_hasMessage() {
        var ex = new InvalidUserTypeUuidException("bad-uuid");
        assertEquals("bad-uuid", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void invalidUserTypeException_hasMessage() {
        var ex = new InvalidUserTypeException("bad-type");
        assertEquals("bad-type", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void invalidUserException_hasMessage() {
        var ex = new InvalidUserException("bad-user");
        assertEquals("bad-user", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void invalidRestaurantException_hasMessage() {
        var ex = new InvalidRestaurantException("bad-rest");
        assertEquals("bad-rest", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }
}

