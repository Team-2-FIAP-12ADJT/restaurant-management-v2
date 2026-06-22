package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllRestaurantsInteractorTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    private CapturingPresenter presenter;
    private GetAllRestaurantsInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetAllRestaurantsInteractor(presenter, restaurantDsGateway);
    }

    @Test
    @DisplayName("Lista restaurantes paginados com sucesso")
    void listsRestaurants() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var dsResponse = new RestaurantDsResponseModel(id, "Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);
        var pageResult = new PageResult<RestaurantDsResponseModel>(List.of(dsResponse), 1L, 1, 10);

        when(restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())).thenReturn(pageResult);

        interactor.execute(new GetAllRestaurantsRequestModel(null, null, 1, 10));

        assertNotNull(presenter.response);
        assertEquals(1, presenter.response.page().content().size());
        assertEquals("Foo", presenter.response.page().content().getFirst().name());
    }

    @Test
    @DisplayName("Retorna página vazia quando não há restaurantes")
    void returnsEmptyPage() {
        var pageResult = new PageResult<RestaurantDsResponseModel>(List.of(), 0L, 1, 10);

        when(restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())).thenReturn(pageResult);

        interactor.execute(new GetAllRestaurantsRequestModel(null, null, 1, 10));

        assertNotNull(presenter.response);
        assertEquals(0, presenter.response.page().content().size());
    }

    @Test
    @DisplayName("Filtra por nome quando informado")
    void filtersByName() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var dsResponse = new RestaurantDsResponseModel(id, "Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);
        var pageResult = new PageResult<RestaurantDsResponseModel>(List.of(dsResponse), 1L, 1, 10);

        when(restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())).thenReturn(pageResult);

        interactor.execute(new GetAllRestaurantsRequestModel("Foo", null, 1, 10));

        assertEquals(1, presenter.response.page().content().size());
        assertEquals("Foo", presenter.response.page().content().getFirst().name());
    }

    private static final class CapturingPresenter implements GetAllRestaurantsOutputBoundary {
        private GetAllRestaurantsResponseModel response;

        @Override
        public void present(GetAllRestaurantsResponseModel response) {
            this.response = response;
        }
    }
}
