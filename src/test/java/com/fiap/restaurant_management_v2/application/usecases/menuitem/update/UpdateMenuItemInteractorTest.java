package com.fiap.restaurant_management_v2.application.usecases.menuitem.update;

import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.domain.exception.InvalidMenuItemException;
import java.math.BigDecimal;
import java.util.Optional;
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

    private static final BigDecimal CURRENT_PRICE = new BigDecimal("39.90");

    @Mock
    private MenuItemDsGateway menuItemDsGateway;

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private LoggerGateway loggerGateway;

    private final TransactionalExecutor transactionalExecutor = Runnable::run;
    private CapturingPresenter presenter;
    private UpdateMenuItemInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new UpdateMenuItemInteractor(
            transactionalExecutor,
            menuItemDsGateway,
            restaurantDsGateway,
            presenter,
            loggerGateway
        );
    }

    @Test
    @DisplayName("PATCH parcial mantém os campos omitidos e usa update, não save")
    void partialUpdateKeepsCurrentFields() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        when(menuItemDsGateway.findById(id))
            .thenReturn(Optional.of(current(id, restaurantId)));
        when(menuItemDsGateway.update(any(MenuItemDsRequestModel.class)))
            .thenAnswer(call -> responseFrom(call.getArgument(0)));

        // Só o nome é enviado; demais campos null = mantém o atual.
        interactor.execute(
            new UpdateMenuItemRequestModel(
                id,
                "Risoto especial",
                null,
                null,
                null,
                null,
                null
            )
        );

        ArgumentCaptor<MenuItemDsRequestModel> captor =
            ArgumentCaptor.forClass(MenuItemDsRequestModel.class);
        verify(menuItemDsGateway).update(captor.capture());
        verify(menuItemDsGateway, never()).save(any());
        verify(restaurantDsGateway, never()).existsById(any());

        MenuItemDsRequestModel merged = captor.getValue();
        assertEquals(id, merged.id());
        assertEquals("Risoto especial", merged.name());
        assertEquals("Risoto de cogumelos", merged.description());
        assertEquals(CURRENT_PRICE, merged.price());
        assertEquals(true, merged.onlyLocal());
        assertEquals("/images/risoto.jpg", merged.photoPath());
        assertEquals(restaurantId, merged.restaurantId());
        assertNotNull(presenter.response);
        assertEquals("Risoto especial", presenter.response.name());
    }

    @Test
    @DisplayName("Troca de restaurante valida a existência do novo restaurante")
    void validatesRestaurantWhenChanged() {
        UUID id = UUID.randomUUID();
        UUID currentRestaurant = UUID.randomUUID();
        UUID newRestaurant = UUID.randomUUID();
        when(menuItemDsGateway.findById(id))
            .thenReturn(Optional.of(current(id, currentRestaurant)));
        when(restaurantDsGateway.existsById(newRestaurant)).thenReturn(true);
        when(menuItemDsGateway.update(any(MenuItemDsRequestModel.class)))
            .thenAnswer(call -> responseFrom(call.getArgument(0)));

        interactor.execute(
            new UpdateMenuItemRequestModel(
                id,
                null,
                null,
                null,
                null,
                null,
                newRestaurant
            )
        );

        ArgumentCaptor<MenuItemDsRequestModel> captor =
            ArgumentCaptor.forClass(MenuItemDsRequestModel.class);
        verify(menuItemDsGateway).update(captor.capture());
        assertEquals(newRestaurant, captor.getValue().restaurantId());
    }

    @Test
    @DisplayName("Restaurante inexistente na troca impede a atualização")
    void rejectsMissingRestaurant() {
        UUID id = UUID.randomUUID();
        UUID newRestaurant = UUID.randomUUID();
        when(menuItemDsGateway.findById(id))
            .thenReturn(Optional.of(current(id, UUID.randomUUID())));
        when(restaurantDsGateway.existsById(newRestaurant)).thenReturn(false);

        assertThrows(
            RestaurantNotFoundException.class,
            () -> interactor.execute(
                new UpdateMenuItemRequestModel(
                    id,
                    null,
                    null,
                    null,
                    null,
                    null,
                    newRestaurant
                )
            )
        );

        verify(menuItemDsGateway, never()).update(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Item inexistente impede a atualização")
    void rejectsMissingMenuItem() {
        UUID id = UUID.randomUUID();
        when(menuItemDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(
            MenuItemNotFoundException.class,
            () -> interactor.execute(
                new UpdateMenuItemRequestModel(
                    id,
                    "Nome",
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        );

        verify(menuItemDsGateway, never()).update(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Estado mesclado inválido não é atualizado")
    void rejectsInvalidMergedState() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        when(menuItemDsGateway.findById(id))
            .thenReturn(Optional.of(current(id, restaurantId)));

        // Preço presente-inválido → estado mesclado inválido → 400.
        assertThrows(
            InvalidMenuItemException.class,
            () -> interactor.execute(
                new UpdateMenuItemRequestModel(
                    id,
                    null,
                    null,
                    BigDecimal.ZERO,
                    null,
                    null,
                    null
                )
            )
        );

        verify(menuItemDsGateway, never()).update(any());
        assertNull(presenter.response);
    }

    private static MenuItemDsResponseModel current(UUID id, UUID restaurantId) {
        return new MenuItemDsResponseModel(
            id,
            "Risoto",
            "Risoto de cogumelos",
            CURRENT_PRICE,
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
        implements UpdateMenuItemOutputBoundary {

        private UpdateMenuItemResponseModel response;

        @Override
        public void present(UpdateMenuItemResponseModel response) {
            this.response = response;
        }
    }
}
