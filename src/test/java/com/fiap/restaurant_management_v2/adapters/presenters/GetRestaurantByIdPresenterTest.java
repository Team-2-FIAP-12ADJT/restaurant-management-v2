package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantWithOwnerViewModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id.GetRestaurantByIdResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetRestaurantByIdPresenterTest {

    @Test
    @DisplayName("Formata response em view model com CNPJ mascarado e owner aninhado")
    void buildsViewModel() {
        var presenter = new GetRestaurantByIdPresenter();
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var owner = new UserDsResponseModel(
            ownerId,
            "Dona Ada",
            "dona@example.com",
            "dona",
            "12345678901",
            UUID.randomUUID(),
            "DONO"
        );

        presenter.present(
            new GetRestaurantByIdResponseModel(
                id,
                "Foo",
                "Rua A",
                "12345678000199",
                "Italiana",
                "Seg-Sex 11h-23h",
                owner
            )
        );

        RestaurantWithOwnerViewModel vm = presenter.getViewModel();
        assertEquals(id.toString(), vm.id());
        assertEquals("Foo", vm.name());
        assertEquals("12.345.678/0001-99", vm.taxIdentifier());
        assertEquals(ownerId.toString(), vm.owner().id());
        assertEquals("123.456.789-01", vm.owner().taxIdentifier());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new GetRestaurantByIdPresenter().getViewModel());
    }
}
