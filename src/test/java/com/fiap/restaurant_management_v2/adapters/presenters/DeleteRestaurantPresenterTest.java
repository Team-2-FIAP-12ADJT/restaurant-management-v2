package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.application.usecases.restaurant.delete.DeleteRestaurantResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteRestaurantPresenterTest {

    @Test
    @DisplayName("Marca como deletado após present()")
    void marksAsDeleted() {
        var presenter = new DeleteRestaurantPresenter();

        assertFalse(presenter.isDeleted());

        presenter.present(new DeleteRestaurantResponseModel(UUID.randomUUID()));

        assertTrue(presenter.isDeleted());
    }
}
