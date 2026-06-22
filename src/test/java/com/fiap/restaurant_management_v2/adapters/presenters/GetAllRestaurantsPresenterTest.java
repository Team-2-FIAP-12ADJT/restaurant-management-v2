package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantViewModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.GetAllRestaurantsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all.RestaurantSummary;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GetAllRestaurantsPresenterTest {

    @Test
    @DisplayName("Formata página de summaries em page view model")
    void buildsPageViewModel() {
        var presenter = new GetAllRestaurantsPresenter();
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var summary = new RestaurantSummary(id, "Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);
        var page = new PageResult<>(List.of(summary), 1L, 1, 10);

        presenter.present(new GetAllRestaurantsResponseModel(page));

        PageViewModel<RestaurantViewModel> vm = presenter.getViewModel();
        assertEquals(1, vm.page());
        assertEquals(1, vm.content().size());
        assertEquals("Foo", vm.content().getFirst().name());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new GetAllRestaurantsPresenter().getViewModel());
    }
}
