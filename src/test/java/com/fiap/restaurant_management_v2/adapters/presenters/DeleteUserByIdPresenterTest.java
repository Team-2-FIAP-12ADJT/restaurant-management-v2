package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.fiap.restaurant_management_v2.application.usecases.user.delete.DeleteUserByIdOutputBoundary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeleteUserByIdPresenterTest {

    @Test
    @DisplayName("present() é no-op (delete retorna 204 sem corpo)")
    void presentIsNoOp() {
        DeleteUserByIdOutputBoundary presenter = new DeleteUserByIdPresenter();

        assertDoesNotThrow(presenter::present);
    }
}
