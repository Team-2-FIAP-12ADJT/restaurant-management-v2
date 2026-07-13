package com.fiap.restaurant_management_v2.application.exception;

/**
 * Lançada ao tentar deletar um restaurante que possui menu items ativos.
 * Mantém o invariante "menu item tem restaurante". Mapeada para 409 Conflict.
 */
public class RestaurantHasActiveMenuItemsException extends RuntimeException {

    public RestaurantHasActiveMenuItemsException(String message) {
        super(message);
    }
}
