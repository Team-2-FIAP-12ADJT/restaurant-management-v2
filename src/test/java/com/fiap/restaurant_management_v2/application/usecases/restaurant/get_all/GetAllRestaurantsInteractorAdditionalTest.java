package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAllRestaurantsInteractorAdditionalTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private GetAllRestaurantsInteractor interactor;

    @BeforeEach
    void setUp() {
        CapturingPresenter presenter = new CapturingPresenter();
        interactor = new GetAllRestaurantsInteractor(
                presenter,
            restaurantDsGateway,
            userDsGateway
        );
    }

    private RestaurantDsResponseModel restaurant(UUID id, UUID ownerId) {
        return new RestaurantDsResponseModel(
            id,
            "Foo",
            "Rua A",
            "12345678000199",
            "Italiana",
            "Seg-Sex 11h-23h",
            ownerId
        );
    }

    @Test
    @DisplayName("Lança UserNotFoundException quando owner da página não é encontrado")
    void throwsWhenOwnerMissing() {
        UUID ownerId = UUID.randomUUID();
        PageResult<RestaurantDsResponseModel> pageResult = new PageResult<>(
            List.of(restaurant(UUID.randomUUID(), ownerId)),
            1L,
            1,
            10
        );
        when(restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt()))
            .thenReturn(pageResult);
        // Simula usuário não encontrado no lote.
        when(userDsGateway.findAllByIds(any())).thenReturn(List.of());

        assertThrows(UserNotFoundException.class, () ->
            interactor.execute(new GetAllRestaurantsRequestModel(null, null, null, 1, 10))
        );
    }

    @Test
    @DisplayName("Não adiciona critério LIKE quando o filtro é blank (spaces)")
    void skipsLikeWhenBlank() {
        PageResult<RestaurantDsResponseModel> pageResult = new PageResult<>(List.of(), 0L, 1, 10);
        var captor = ArgumentCaptor.forClass(SearchQuery.class);
        when(restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt()))
            .thenReturn(pageResult);
        when(userDsGateway.findAllByIds(any())).thenReturn(List.of());

        interactor.execute(new GetAllRestaurantsRequestModel("   ", null, null, 1, 10));

        verify(restaurantDsGateway).findAll(captor.capture(), anyInt(), anyInt());
        var criteria = captor.getValue().criteria();
        assertEquals(0, criteria.size());
    }

    @Test
    @DisplayName("Não adiciona critério CNPJ quando o filtro é blank (spaces)")
    void skipsCnpjWhenBlank() {
        PageResult<RestaurantDsResponseModel> pageResult = new PageResult<>(List.of(), 0L, 1, 10);
        var captor = ArgumentCaptor.forClass(SearchQuery.class);
        when(restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt()))
            .thenReturn(pageResult);
        when(userDsGateway.findAllByIds(any())).thenReturn(List.of());

        interactor.execute(new GetAllRestaurantsRequestModel(null, "   ", null, 1, 10));

        verify(restaurantDsGateway).findAll(captor.capture(), anyInt(), anyInt());
        var criteria = captor.getValue().criteria();
        assertEquals(0, criteria.size());
    }

    private static final class CapturingPresenter implements GetAllRestaurantsOutputBoundary {
        @Override
        public void present(GetAllRestaurantsResponseModel response) {
            // Sem ação para estes testes.
        }
    }
}


