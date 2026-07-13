package com.fiap.restaurant_management_v2.application.usecases.user.get_all;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterOperator;
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
class GetAllUsersInteractorTest {

    @Mock
    private UserDsGateway userDsGateway;

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    private CapturingPresenter presenter;
    private GetAllUsersInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetAllUsersInteractor(presenter, userDsGateway, restaurantDsGateway);
    }

    @Test
    @DisplayName("Lista usuários paginados com sucesso")
    void listsUsers() {
        var id = UUID.randomUUID();
        var userTypeId = UUID.randomUUID();
        var user = new UserDsResponseModel(
            id,
            "Foo",
            "foo@example.com",
            "foo",
            "12345678900",
            userTypeId,
            "DONO"
        );
        var pageResult = new PageResult<>(List.of(user), 1L, 1, 10);

        when(
            userDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(restaurantDsGateway.findAllByOwnerIds(List.of(id))).thenReturn(List.of());

        interactor.execute(
            new GetAllUsersRequestModel(null, null, null, null, 1, 10)
        );

        assertNotNull(presenter.response);
        assertEquals(1, presenter.response.page().content().size());
        assertEquals(
            "Foo",
            presenter.response.page().content().getFirst().name()
        );
    }

    @Test
    @DisplayName("Retorna página vazia quando não há usuários")
    void returnsEmptyPage() {
        var pageResult = new PageResult<UserDsResponseModel>(
            List.of(),
            0L,
            1,
            10
        );

        when(
            userDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(restaurantDsGateway.findAllByOwnerIds(List.of())).thenReturn(List.of());

        interactor.execute(
            new GetAllUsersRequestModel(null, null, null, null, 1, 10)
        );

        assertNotNull(presenter.response);
        assertEquals(0, presenter.response.page().content().size());
    }

    @Test
    @DisplayName("Filtra por nome (monta LIKE name=valor) quando informado")
    void filtersByName() {
        var id = UUID.randomUUID();
        var userTypeId = UUID.randomUUID();
        var user = new UserDsResponseModel(
            id,
            "Foo",
            "foo@example.com",
            "foo",
            "12345678900",
            userTypeId,
            "DONO"
        );
        var pageResult = new PageResult<>(List.of(user), 1L, 1, 10);
        var queryCaptor = ArgumentCaptor.forClass(SearchQuery.class);

        when(
            userDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(restaurantDsGateway.findAllByOwnerIds(List.of(id))).thenReturn(List.of());

        interactor.execute(
            new GetAllUsersRequestModel("Foo", null, null, null, 1, 10)
        );

        verify(userDsGateway).findAll(queryCaptor.capture(), eq(1), eq(10));
        var criteria = queryCaptor.getValue().criteria();
        assertEquals(1, criteria.size());
        var filter = criteria.getFirst();
        assertEquals("name", filter.field());
        assertEquals(FilterOperator.LIKE, filter.operator());
        assertEquals("Foo", filter.value());

        assertEquals(1, presenter.response.page().content().size());
        assertEquals(
            "Foo",
            presenter.response.page().content().getFirst().name()
        );
    }

    @Test
    @DisplayName("Filtra por CPF mascarado (normaliza p/ dígitos crus)")
    void filtersByTaxNormalizingMask() {
        var pageResult = new PageResult<UserDsResponseModel>(
            List.of(),
            0L,
            1,
            10
        );
        var queryCaptor = ArgumentCaptor.forClass(SearchQuery.class);
        when(
            userDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(restaurantDsGateway.findAllByOwnerIds(List.of())).thenReturn(List.of());

        interactor.execute(
            new GetAllUsersRequestModel(
                null,
                null,
                null,
                "123.456.789-01",
                1,
                10
            )
        );

        verify(userDsGateway).findAll(queryCaptor.capture(), eq(1), eq(10));
        var criteria = queryCaptor.getValue().criteria();
        assertEquals(1, criteria.size());
        assertEquals("taxIdentifier", criteria.getFirst().field());
        assertEquals("12345678901", criteria.getFirst().value());
    }

    @ParameterizedTest
    @DisplayName(
        "Filtro CPF sem dígito (letras ou wildcard SQL) vira sentinela impossível"
    )
    @ValueSource(strings = { "abc", "%", "_", "..." })
    void taxFilterWithoutDigitsBecomesImpossibleSentinel(String filter) {
        var pageResult = new PageResult<UserDsResponseModel>(
            List.of(),
            0L,
            1,
            10
        );
        var queryCaptor = ArgumentCaptor.forClass(SearchQuery.class);
        when(
            userDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(restaurantDsGateway.findAllByOwnerIds(List.of())).thenReturn(List.of());

        interactor.execute(
            new GetAllUsersRequestModel(null, null, null, filter, 1, 10)
        );

        verify(userDsGateway).findAll(queryCaptor.capture(), eq(1), eq(10));
        var criteria = queryCaptor.getValue().criteria();
        // Critério presente (não descartado) e SEM caractere curinga cru => não casa CPF.
        assertEquals(1, criteria.size());
        var value = criteria.getFirst().value();
        assertEquals("taxIdentifier", criteria.getFirst().field());
        assertEquals("NON-CPF", value);
        assertFalse(value.contains("%"));
        assertFalse(value.contains("_"));
    }

    @Test
    @DisplayName("Ignora filtros blank para name/email/login (não monta criteria)")
    void ignoresBlankLikeFilters() {
        var pageResult = new PageResult<UserDsResponseModel>(
            List.of(),
            0L,
            1,
            10
        );
        var queryCaptor = ArgumentCaptor.forClass(SearchQuery.class);
        when(
            userDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(restaurantDsGateway.findAllByOwnerIds(List.of())).thenReturn(List.of());

        // Passa vários valores em branco para nome/email/login.
        interactor.execute(
            new GetAllUsersRequestModel("  ", "\t", "", null, 1, 10)
        );

        verify(userDsGateway).findAll(queryCaptor.capture(), eq(1), eq(10));
        var criteria = queryCaptor.getValue().criteria();
        assertEquals(0, criteria.size());
    }

    @Test
    @DisplayName("Ignora filtro taxIdentifier blank (não monta criteria)")
    void ignoresBlankTaxFilter() {
        var pageResult = new PageResult<UserDsResponseModel>(
            List.of(),
            0L,
            1,
            10
        );
        var queryCaptor = ArgumentCaptor.forClass(SearchQuery.class);
        when(
            userDsGateway.findAll(any(SearchQuery.class), anyInt(), anyInt())
        ).thenReturn(pageResult);
        when(restaurantDsGateway.findAllByOwnerIds(List.of())).thenReturn(List.of());

        interactor.execute(
            new GetAllUsersRequestModel(null, null, null, "  ", 1, 10)
        );

        verify(userDsGateway).findAll(queryCaptor.capture(), eq(1), eq(10));
        var criteria = queryCaptor.getValue().criteria();
        assertEquals(0, criteria.size());
    }

    private static final class CapturingPresenter
        implements GetAllUsersOutputBoundary
    {

        private GetAllUsersResponseModel response;

        @Override
        public void present(GetAllUsersResponseModel response) {
            this.response = response;
        }
    }
}
