package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.update.UpdateRestaurantResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateRestaurantPresenterTest {

    @Test
    @DisplayName("Formata response model em view model")
    void buildsViewModel() {
        var presenter = new UpdateRestaurantPresenter();
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();

        presenter.present(new UpdateRestaurantResponseModel(id, "Bar", "Rua B", "Japonesa", "Seg-Sab 12h-22h", ownerId));

        RestaurantViewModel vm = presenter.getViewModel();
        assertEquals(id.toString(), vm.id());
        assertEquals("Bar", vm.name());
        assertEquals("Japonesa", vm.cuisineType());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new UpdateRestaurantPresenter().getViewModel());
    }
}
