package com.fiap.restaurant_management_v2.application.usecases.menuitem.update;

import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.domain.exception.InvalidMenuItemException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateMenuItemInteractorTest {

    @Mock
    private MenuItemDsGateway menuItemDsGateway;

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private LoggerGateway loggerGateway;

    private CapturingPresenter presenter;
    private UpdateMenuItemInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new UpdateMenuItemInteractor(
            menuItemDsGateway,
            restaurantDsGateway,
            presenter,
            loggerGateway
        );
    }

    @Test
    @DisplayName("Atualiza item do cardápio com sucesso")
    void updatesMenuItemSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UpdateMenuItemRequestModel request = validRequest(id, restaurantId);

        when(menuItemDsGateway.existsById(id)).thenReturn(true);
        when(restaurantDsGateway.existsById(restaurantId)).thenReturn(true);
        when(menuItemDsGateway.save(any(MenuItemDsRequestModel.class)))
            .thenAnswer(call -> {
                MenuItemDsRequestModel item = call.getArgument(0);
                return responseFrom(item);
            });

        interactor.execute(request);

        ArgumentCaptor<MenuItemDsRequestModel> captor =
            ArgumentCaptor.forClass(MenuItemDsRequestModel.class);
        verify(menuItemDsGateway).save(captor.capture());

        assertEquals(id, captor.getValue().id());
        assertEquals("Risoto especial", captor.getValue().name());
        assertNotNull(presenter.response);
        assertEquals(id, presenter.response.id());
        assertEquals("Risoto especial", presenter.response.name());
    }

    @Test
    @DisplayName("Item inexistente impede a atualização")
    void rejectsMissingMenuItem() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        when(menuItemDsGateway.existsById(id)).thenReturn(false);

        assertThrows(
            MenuItemNotFoundException.class,
            () -> interactor.execute(validRequest(id, restaurantId))
        );

        verify(restaurantDsGateway, never()).existsById(any());
        verify(menuItemDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Restaurante inexistente impede a atualização")
    void rejectsMissingRestaurant() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        when(menuItemDsGateway.existsById(id)).thenReturn(true);
        when(restaurantDsGateway.existsById(restaurantId)).thenReturn(false);

        assertThrows(
            RestaurantNotFoundException.class,
            () -> interactor.execute(validRequest(id, restaurantId))
        );

        verify(menuItemDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Dados inválidos não são atualizados")
    void rejectsInvalidMenuItem() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        when(menuItemDsGateway.existsById(id)).thenReturn(true);
        when(restaurantDsGateway.existsById(restaurantId)).thenReturn(true);

        UpdateMenuItemRequestModel request = new UpdateMenuItemRequestModel(
            id,
            "Risoto",
            "Risoto de cogumelos",
            BigDecimal.ZERO,
            false,
            "/images/risoto.jpg",
            restaurantId
        );

        assertThrows(
            InvalidMenuItemException.class,
            () -> interactor.execute(request)
        );

        verify(menuItemDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    private static UpdateMenuItemRequestModel validRequest(
        UUID id,
        UUID restaurantId
    ) {
        return new UpdateMenuItemRequestModel(
            id,
            "Risoto especial",
            "Risoto de cogumelos",
            new BigDecimal("49.90"),
            false,
            "/images/risoto-especial.jpg",
            restaurantId
        );
    }

    private static MenuItemDsResponseModel responseFrom(
        MenuItemDsRequestModel item
    ) {
        return new MenuItemDsResponseModel(
            item.id(),
            item.name(),
            item.description(),
            item.price(),
            item.onlyLocal(),
            item.photoPath(),
            item.restaurantId()
        );
    }

    private static final class CapturingPresenter
        implements UpdateMenuItemOutputBoundary {

        private UpdateMenuItemResponseModel response;

        @Override
        public void present(UpdateMenuItemResponseModel response) {
            this.response = response;
        }
    }
}
