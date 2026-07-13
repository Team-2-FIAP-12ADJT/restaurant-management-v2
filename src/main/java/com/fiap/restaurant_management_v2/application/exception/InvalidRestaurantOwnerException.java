package com.fiap.restaurant_management_v2.application.exception;

/**
 * Lançada quando proprietário de restaurante não é do tipo Dono. Mantém o
 * invariante "only Dono can own restaurant". Mapeada para 422 Unprocessable Entity.
 */
public class InvalidRestaurantOwnerException extends RuntimeException {

    public InvalidRestaurantOwnerException(String message) {
        super(message);
    }
}
