package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.create.CreateRestaurantResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreateRestaurantPresenterTest {

    @Test
    @DisplayName("Formata o response model em view model com ids como string")
    void buildsViewModelFromResponse() {
        var presenter = new CreateRestaurantPresenter();
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();

        presenter.present(new CreateRestaurantResponseModel(id, "Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId));

        RestaurantViewModel vm = presenter.getViewModel();
        assertEquals(id.toString(), vm.id());
        assertEquals("Foo", vm.name());
        assertEquals("Rua A", vm.address());
        assertEquals("Italiana", vm.cuisineType());
        assertEquals("Seg-Sex 11h-23h", vm.openingHours());
        assertEquals(ownerId.toString(), vm.ownerId());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new CreateRestaurantPresenter().getViewModel());
    }
}
