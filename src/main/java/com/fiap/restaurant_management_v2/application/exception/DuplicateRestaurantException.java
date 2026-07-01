package com.fiap.restaurant_management_v2.application.exception;

public class DuplicateRestaurantException extends RuntimeException {

    public DuplicateRestaurantException(String message) {
        super(message);
    }
}
