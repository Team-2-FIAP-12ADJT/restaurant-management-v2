package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantWithOwnerViewModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.RestaurantSummary;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetAllRestaurantsPresenterTest {

    @Test
    @DisplayName("Formata página de summaries com CNPJ mascarado e owner aninhado")
    void buildsPageViewModel() {
        var presenter = new GetAllRestaurantsPresenter();
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
        var summary = new RestaurantSummary(
            id,
            "Foo",
            "Rua A",
            "12345678000199",
            "Italiana",
            "Seg-Sex 11h-23h",
            owner
        );
        var page = new PageResult<>(List.of(summary), 1L, 1, 10);

        presenter.present(new GetAllRestaurantsResponseModel(page));

        PageViewModel<RestaurantWithOwnerViewModel> vm = presenter.getViewModel();
        assertEquals(1, vm.page());
        assertEquals(1, vm.content().size());
        var first = vm.content().getFirst();
        assertEquals("Foo", first.name());
        assertEquals("12.345.678/0001-99", first.taxIdentifier());
        assertEquals(ownerId.toString(), first.owner().id());
        assertEquals("123.456.789-01", first.owner().taxIdentifier());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new GetAllRestaurantsPresenter().getViewModel());
    }
}
