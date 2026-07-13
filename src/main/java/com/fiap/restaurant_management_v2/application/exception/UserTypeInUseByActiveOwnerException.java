package com.fiap.restaurant_management_v2.application.exception;

/**
 * Lançada ao tentar deletar um UserType se existir user ativo vinculado a ele
 * que seja proprietário de restaurante ativo. Mantém o invariante
 * "restaurante tem dono com userType válido". Mapeada para 409 Conflict.
 */
public class UserTypeInUseByActiveOwnerException extends RuntimeException {

    public UserTypeInUseByActiveOwnerException(String message) {
        super(message);
    }
}
