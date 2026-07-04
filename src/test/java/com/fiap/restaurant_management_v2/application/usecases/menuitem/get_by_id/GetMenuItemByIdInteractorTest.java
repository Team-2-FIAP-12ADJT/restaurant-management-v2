package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id;

import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import java.math.BigDecimal;
import java.util.Optional;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMenuItemByIdInteractorTest {

    @Mock
    private MenuItemDsGateway menuItemDsGateway;

    private CapturingPresenter presenter;
    private GetMenuItemByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetMenuItemByIdInteractor(
            menuItemDsGateway,
            presenter
        );
    }

    @Test
    @DisplayName("Retorna item do cardápio quando encontrado")
    void returnsMenuItemWhenFound() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        MenuItemDsResponseModel item = new MenuItemDsResponseModel(
            id,
            "Risoto",
            "Risoto de cogumelos",
            new BigDecimal("39.90"),
            true,
            "/images/risoto.jpg",
            restaurantId
        );
        when(menuItemDsGateway.findById(id)).thenReturn(Optional.of(item));

        interactor.execute(new GetMenuItemByIdRequestModel(id));

        assertNotNull(presenter.response);
        assertEquals(id, presenter.response.id());
        assertEquals("Risoto", presenter.response.name());
        assertEquals(restaurantId, presenter.response.restaurantId());
    }

    @Test
    @DisplayName("Item inexistente lança exceção")
    void throwsWhenMenuItemIsMissing() {
        UUID id = UUID.randomUUID();
        when(menuItemDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(
            MenuItemNotFoundException.class,
            () -> interactor.execute(new GetMenuItemByIdRequestModel(id))
        );
    }

    private static final class CapturingPresenter
        implements GetMenuItemByIdOutputBoundary {

        private GetMenuItemByIdResponseModel response;

        @Override
        public void present(GetMenuItemByIdResponseModel response) {
            this.response = response;
        }
    }
}
