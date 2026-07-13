package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAllRestaurantsInteractorTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private CapturingPresenter presenter;
    private GetAllRestaurantsInteractor interactor;

    private UUID ownerId;
    private UserDsResponseModel owner;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetAllRestaurantsInteractor(
            presenter,
            restaurantDsGateway,
            userDsGateway
        );
        ownerId = UUID.randomUUID();
        owner = new UserDsResponseModel(
            ownerId,
            "Dona Ada",
            "dona@example.com",
            "dona",
            "12345678901",
            UUID.randomUUID(),
            "DONO"
        );
    }

    private RestaurantDsResponseModel restaurant(UUID id) {
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
    @DisplayName("Lista restaurantes paginados com owner completo (batch)")
    void listsRestaurants() {
        var pageResult = new PageResult<>(
            List.of(restaurant(UUID.randomUUID())),
            1L,
            1,
            10
        );
        when(
            restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(userDsGateway.findAllByIds(any())).thenReturn(List.of(owner));

        interactor.execute(
            new GetAllRestaurantsRequestModel(null, null, null, 1, 10)
        );

        assertNotNull(presenter.response);
        assertEquals(1, presenter.response.page().content().size());
        var first = presenter.response.page().content().getFirst();
        assertEquals("Foo", first.name());
        assertEquals(owner, first.owner());
    }

    @Test
    @DisplayName("Retorna página vazia quando não há restaurantes")
    void returnsEmptyPage() {
        var pageResult = new PageResult<RestaurantDsResponseModel>(
            List.of(),
            0L,
            1,
            10
        );
        when(
            restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(userDsGateway.findAllByIds(any())).thenReturn(List.of());

        interactor.execute(
            new GetAllRestaurantsRequestModel(null, null, null, 1, 10)
        );

        assertNotNull(presenter.response);
        assertEquals(0, presenter.response.page().content().size());
    }

    @Test
    @DisplayName("Filtra por nome quando informado")
    void filtersByName() {
        var pageResult = new PageResult<>(
            List.of(restaurant(UUID.randomUUID())),
            1L,
            1,
            10
        );
        when(
            restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(userDsGateway.findAllByIds(any())).thenReturn(List.of(owner));

        interactor.execute(
            new GetAllRestaurantsRequestModel("Foo", null, null, 1, 10)
        );

        assertEquals(1, presenter.response.page().content().size());
        assertEquals(
            "Foo",
            presenter.response.page().content().getFirst().name()
        );
    }

    @Test
    @DisplayName("Filtra por CNPJ mascarado → critério normalizado (só alfanumérico)")
    void filtersByCnpjNormalizingMask() {
        var pageResult = new PageResult<RestaurantDsResponseModel>(
            List.of(),
            0L,
            1,
            10
        );
        var captor = ArgumentCaptor.forClass(SearchQuery.class);
        when(
            restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(userDsGateway.findAllByIds(any())).thenReturn(List.of());

        interactor.execute(
            new GetAllRestaurantsRequestModel(
                null,
                "12.345.678/0001-99",
                null,
                1,
                10
            )
        );

        verify(restaurantDsGateway).findAll(captor.capture(), eq(1), eq(10));
        var criteria = captor.getValue().criteria();
        assertEquals(1, criteria.size());
        assertEquals("taxIdentifier", criteria.getFirst().field());
        assertEquals("12345678000199", criteria.getFirst().value());
    }

    @ParameterizedTest
    @DisplayName(
        "Filtro CNPJ sem alfanumérico (wildcard/pontuação) vira sentinela NON-CNPJ"
    )
    @ValueSource(strings = { "%", "_", "...", "//--" })
    void cnpjFilterWithoutAlnumBecomesSentinel(String filter) {
        var pageResult = new PageResult<RestaurantDsResponseModel>(
            List.of(),
            0L,
            1,
            10
        );
        var captor = ArgumentCaptor.forClass(SearchQuery.class);
        when(
            restaurantDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(userDsGateway.findAllByIds(any())).thenReturn(List.of());

        interactor.execute(
            new GetAllRestaurantsRequestModel(null, filter, null, 1, 10)
        );

        verify(restaurantDsGateway).findAll(captor.capture(), eq(1), eq(10));
        var criteria = captor.getValue().criteria();
        assertEquals(1, criteria.size());
        var value = criteria.getFirst().value();
        assertEquals("taxIdentifier", criteria.getFirst().field());
        assertEquals("NON-CNPJ", value);
    }

    private static final class CapturingPresenter
        implements GetAllRestaurantsOutputBoundary
    {

        private GetAllRestaurantsResponseModel response;

        @Override
        public void present(GetAllRestaurantsResponseModel response) {
            this.response = response;
        }
    }
}
