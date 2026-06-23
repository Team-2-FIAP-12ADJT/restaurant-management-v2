package com.fiap.restaurant_management_v2.domain.exception;

public class InvalidMenuItemException extends RuntimeException {
    public InvalidMenuItemException(String message) {
        super(message);
    }
}
