package com.fiap.restaurant_management_v2.domain.exception;

/** Raised when a {@code User} would violate a domain invariant. */
public class InvalidUserTypeException extends RuntimeException {
    public InvalidUserTypeException(String message) {
        super(message);
    }
}
