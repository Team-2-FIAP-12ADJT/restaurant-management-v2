package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.math.BigDecimal;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMenuItemsByRestaurantInteractorTest {

    @Mock
    private MenuItemDsGateway menuItemDsGateway;

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    private CapturingPresenter presenter;
    private GetMenuItemsByRestaurantInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetMenuItemsByRestaurantInteractor(
            presenter,
            menuItemDsGateway,
            restaurantDsGateway
        );
    }

    @Test
    @DisplayName("Lista itens vinculados ao restaurante")
    void listsMenuItemsByRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        MenuItemDsResponseModel item = new MenuItemDsResponseModel(
            UUID.randomUUID(),
            "Risoto",
            "Risoto de cogumelos",
            new BigDecimal("39.90"),
            true,
            "/images/risoto.jpg",
            restaurantId
        );
        PageResult<MenuItemDsResponseModel> page = new PageResult<>(
            List.of(item),
            1L,
            1,
            10
        );

        when(restaurantDsGateway.existsById(restaurantId)).thenReturn(true);
        when(menuItemDsGateway.findAllByRestaurant(restaurantId, 1, 10))
            .thenReturn(page);

        interactor.execute(
            new GetMenuItemsByRestaurantRequestModel(restaurantId, 1, 10)
        );

        verify(menuItemDsGateway).findAllByRestaurant(restaurantId, 1, 10);
        assertNotNull(presenter.response);
        assertEquals(1, presenter.response.page().content().size());
        assertEquals(
            restaurantId,
            presenter.response.page().content().getFirst().restaurantId()
        );
    }

    @Test
    @DisplayName("Retorna página vazia para restaurante sem itens")
    void returnsEmptyPage() {
        UUID restaurantId = UUID.randomUUID();
        PageResult<MenuItemDsResponseModel> page = new PageResult<>(
            List.of(),
            0L,
            1,
            10
        );

        when(restaurantDsGateway.existsById(restaurantId)).thenReturn(true);
        when(menuItemDsGateway.findAllByRestaurant(restaurantId, 1, 10))
            .thenReturn(page);

        interactor.execute(
            new GetMenuItemsByRestaurantRequestModel(restaurantId, 1, 10)
        );

        assertEquals(0, presenter.response.page().content().size());
    }

    @Test
    @DisplayName("Restaurante inexistente impede a consulta")
    void rejectsMissingRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantDsGateway.existsById(restaurantId)).thenReturn(false);

        assertThrows(
            RestaurantNotFoundException.class,
            () -> interactor.execute(
                new GetMenuItemsByRestaurantRequestModel(restaurantId, 1, 10)
            )
        );

        verify(menuItemDsGateway, never())
            .findAllByRestaurant(any(), anyInt(), anyInt());
    }

    private static final class CapturingPresenter
        implements GetMenuItemsByRestaurantOutputBoundary {

        private GetMenuItemsByRestaurantResponseModel response;

        @Override
        public void present(GetMenuItemsByRestaurantResponseModel response) {
            this.response = response;
        }
    }
}
