package com.fiap.restaurant_management_v2.domain.exception;

/** Raised when a {@code User} would violate a domain invariant. */
public class InvalidUserTypeUuidException extends RuntimeException {
    public InvalidUserTypeUuidException(String message) {
        super(message);
    }
}
