package com.fiap.restaurant_management_v2.application.exception;

/**
 * Lançada ao tentar deletar um User que ainda possui restaurante ativo. Mantém o
 * invariante "restaurante tem dono" (owner_id NOT NULL) — bloqueia o delete em vez
 * de orfanar o restaurante. Mapeada para 409 Conflict.
 */
public class UserHasActiveRestaurantsException extends RuntimeException {

    public UserHasActiveRestaurantsException(String message) {
        super(message);
    }
}
