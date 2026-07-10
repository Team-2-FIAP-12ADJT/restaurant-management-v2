package com.fiap.restaurant_management_v2.application.usecases.restaurant.delete;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteRestaurantInteractorTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private LoggerGateway loggerGateway;

    private CapturingPresenter presenter;
    private DeleteRestaurantInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new DeleteRestaurantInteractor(
            restaurantDsGateway,
            presenter,
            loggerGateway
        );
    }

    @Test
    @DisplayName("Exclui restaurante com sucesso")
    void deletesSuccessfully() {
        var id = UUID.randomUUID();

        when(restaurantDsGateway.existsById(id)).thenReturn(true);

        interactor.execute(new DeleteRestaurantRequestModel(id));

        verify(restaurantDsGateway).deleteById(id);
        assertTrue(presenter.deleted);
    }

    @Test
    @DisplayName("Restaurante inexistente lança exceção")
    void throwsWhenNotFound() {
        var id = UUID.randomUUID();

        when(restaurantDsGateway.existsById(id)).thenReturn(false);

        assertThrows(RestaurantNotFoundException.class, () ->
            interactor.execute(new DeleteRestaurantRequestModel(id))
        );
    }

    private static final class CapturingPresenter
        implements DeleteRestaurantOutputBoundary
    {

        private boolean deleted;

        @Override
        public void present(DeleteRestaurantResponseModel response) {
            this.deleted = true;
        }
    }
}
