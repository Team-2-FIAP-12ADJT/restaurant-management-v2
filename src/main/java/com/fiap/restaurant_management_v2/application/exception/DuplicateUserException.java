package com.fiap.restaurant_management_v2.application.exception;

/** Raised when a user with the same email or login already exists. */
public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
