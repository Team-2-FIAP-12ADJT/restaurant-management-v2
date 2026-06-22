package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GetRestaurantByIdPresenterTest {

    @Test
    @DisplayName("Formata response model em view model")
    void buildsViewModel() {
        var presenter = new GetRestaurantByIdPresenter();
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();

        presenter.present(new GetRestaurantByIdResponseModel(id, "Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId));

        RestaurantViewModel vm = presenter.getViewModel();
        assertEquals(id.toString(), vm.id());
        assertEquals("Foo", vm.name());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new GetRestaurantByIdPresenter().getViewModel());
    }
}
