package com.fiap.restaurant_management_v2.application.exception;

public class InvalidFilterException extends RuntimeException {

    public InvalidFilterException(String field) {
        super("Unknown filter field: " + field);
    }
}
