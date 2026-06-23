package com.fiap.restaurant_management_v2.application.usecases.menuitem.delete;

import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteMenuItemInteractorTest {

    @Mock
    private MenuItemDsGateway menuItemDsGateway;

    private CapturingPresenter presenter;
    private DeleteMenuItemInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new DeleteMenuItemInteractor(menuItemDsGateway, presenter);
    }

    @Test
    @DisplayName("Exclui item do cardápio existente")
    void deletesMenuItemSuccessfully() {
        UUID id = UUID.randomUUID();
        when(menuItemDsGateway.existsById(id)).thenReturn(true);

        interactor.execute(new DeleteMenuItemRequestModel(id));

        verify(menuItemDsGateway).deleteById(id);
        assertEquals(id, presenter.response.id());
    }

    @Test
    @DisplayName("Item inexistente não é excluído")
    void rejectsMissingMenuItem() {
        UUID id = UUID.randomUUID();
        when(menuItemDsGateway.existsById(id)).thenReturn(false);

        assertThrows(
            MenuItemNotFoundException.class,
            () -> interactor.execute(new DeleteMenuItemRequestModel(id))
        );

        verify(menuItemDsGateway, never()).deleteById(id);
        assertNull(presenter.response);
    }

    private static final class CapturingPresenter
        implements DeleteMenuItemOutputBoundary {

        private DeleteMenuItemResponseModel response;

        @Override
        public void present(DeleteMenuItemResponseModel response) {
            this.response = response;
        }
    }
}
