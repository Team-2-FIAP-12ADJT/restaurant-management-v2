package com.fiap.restaurant_management_v2.application.exception;

public class DuplicateUserTypeException extends RuntimeException {
    public DuplicateUserTypeException(String message) {
        super(message);
    }
}
