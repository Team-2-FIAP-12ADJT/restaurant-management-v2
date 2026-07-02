package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantWithOwnerViewModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.update.UpdateRestaurantResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateRestaurantPresenterTest {

    @Test
    @DisplayName("Formata response model em view model com owner aninhado e CPF formatado")
    void buildsViewModelWithNestedOwner() {
        var presenter = new UpdateRestaurantPresenter();
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var owner = new UserDsResponseModel(
            ownerId,
            "Dona Ada",
            "dona@example.com",
            "dona",
            "12345678901"
        );

        presenter.present(
            new UpdateRestaurantResponseModel(
                id,
                "Bar",
                "Rua B",
                "12345678000199",
                "Japonesa",
                "Seg-Sab 12h-22h",
                owner
            )
        );

        RestaurantWithOwnerViewModel vm = presenter.getViewModel();
        assertEquals(id.toString(), vm.id());
        assertEquals("Bar", vm.name());
        assertEquals("Japonesa", vm.cuisineType());
        // owner completo aninhado
        assertEquals(ownerId.toString(), vm.owner().id());
        assertEquals("Dona Ada", vm.owner().name());
        assertEquals("dona@example.com", vm.owner().email());
        assertEquals("dona", vm.owner().login());
        // CPF do owner formatado (reuso CpfFormatter)
        assertEquals("123.456.789-01", vm.owner().taxIdentifier());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new UpdateRestaurantPresenter().getViewModel());
    }
}
