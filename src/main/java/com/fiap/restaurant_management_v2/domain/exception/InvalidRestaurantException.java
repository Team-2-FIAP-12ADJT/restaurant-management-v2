package com.fiap.restaurant_management_v2.domain.exception;

public class InvalidRestaurantException extends RuntimeException {
    public InvalidRestaurantException(String message) {
        super(message);
    }
}
