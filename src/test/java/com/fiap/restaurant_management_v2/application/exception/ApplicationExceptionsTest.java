package com.fiap.restaurant_management_v2.application.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationExceptionsTest {

    @Test
    void duplicateUserException_hasMessage() {
        var ex = new DuplicateUserException("dup");
        assertEquals("dup", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void duplicateUserTypeException_hasMessage() {
        var ex = new DuplicateUserTypeException("dup-type");
        assertEquals("dup-type", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void duplicateRestaurantException_hasMessage() {
        var ex = new DuplicateRestaurantException("dup-rest");
        assertEquals("dup-rest", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void restaurantNotFoundException_hasMessage() {
        var ex = new RestaurantNotFoundException("no-rest");
        assertEquals("no-rest", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void invalidFilterException_constructsMessage() {
        var ex = new InvalidFilterException("foo");
        assertEquals("Unknown filter field: foo", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void userHasActiveRestaurantsException_hasMessage() {
        var ex = new UserHasActiveRestaurantsException("owner-has-rest");
        assertEquals("owner-has-rest", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void userTypeNotFoundException_hasMessage() {
        var ex = new UserTypeNotFoundException("no-type");
        assertEquals("no-type", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void userNotFoundException_hasMessage() {
        var ex = new UserNotFoundException("no-user");
        assertEquals("no-user", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }
}

