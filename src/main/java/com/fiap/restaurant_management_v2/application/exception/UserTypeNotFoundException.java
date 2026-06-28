package com.fiap.restaurant_management_v2.application.exception;

public class UserTypeNotFoundException extends RuntimeException {

    public UserTypeNotFoundException(String message) {
        super(message);
    }
}
