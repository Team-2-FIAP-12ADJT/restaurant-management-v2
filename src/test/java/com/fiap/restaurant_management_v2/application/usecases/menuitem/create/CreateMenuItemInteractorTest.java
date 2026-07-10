package com.fiap.restaurant_management_v2.application.usecases.menuitem.create;

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
class CreateMenuItemInteractorTest {

    @Mock
    private MenuItemDsGateway menuItemDsGateway;

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private LoggerGateway loggerGateway;

    private CapturingPresenter presenter;
    private CreateMenuItemInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new CreateMenuItemInteractor(
            menuItemDsGateway,
            restaurantDsGateway,
            presenter,
            loggerGateway
        );
    }

    @Test
    @DisplayName("Cadastra item do cardápio para restaurante existente")
    void createsMenuItemSuccessfully() {
        UUID restaurantId = UUID.randomUUID();
        CreateMenuItemRequestModel request = validRequest(restaurantId);

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

        assertNotNull(captor.getValue().id());
        assertEquals("Risoto", captor.getValue().name());
        assertEquals(restaurantId, captor.getValue().restaurantId());
        assertNotNull(presenter.response);
        assertEquals("Risoto", presenter.response.name());
        assertEquals(new BigDecimal("39.90"), presenter.response.price());
    }

    @Test
    @DisplayName("Restaurante inexistente impede o cadastro")
    void rejectsMissingRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantDsGateway.existsById(restaurantId)).thenReturn(false);

        assertThrows(
            RestaurantNotFoundException.class,
            () -> interactor.execute(validRequest(restaurantId))
        );

        verify(menuItemDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Dados inválidos não são persistidos")
    void rejectsInvalidMenuItem() {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantDsGateway.existsById(restaurantId)).thenReturn(true);

        CreateMenuItemRequestModel request = new CreateMenuItemRequestModel(
            "",
            "Risoto de cogumelos",
            new BigDecimal("39.90"),
            true,
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

    private static CreateMenuItemRequestModel validRequest(UUID restaurantId) {
        return new CreateMenuItemRequestModel(
            "Risoto",
            "Risoto de cogumelos",
            new BigDecimal("39.90"),
            true,
            "/images/risoto.jpg",
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
        implements CreateMenuItemOutputBoundary {

        private CreateMenuItemResponseModel response;

        @Override
        public void present(CreateMenuItemResponseModel response) {
            this.response = response;
        }
    }
}
