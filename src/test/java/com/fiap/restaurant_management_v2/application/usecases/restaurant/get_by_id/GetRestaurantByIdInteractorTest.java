package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
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
class GetRestaurantByIdInteractorTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    private CapturingPresenter presenter;
    private GetRestaurantByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetRestaurantByIdInteractor(restaurantDsGateway, presenter);
    }

    @Test
    @DisplayName("Retorna restaurante quando encontrado")
    void returnsRestaurantWhenFound() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var dsResponse = new RestaurantDsResponseModel(id, "Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);

        when(restaurantDsGateway.findById(id)).thenReturn(Optional.of(dsResponse));

        interactor.execute(new GetRestaurantByIdRequestModel(id));

        assertNotNull(presenter.response);
        assertEquals("Foo", presenter.response.name());
        assertEquals(id, presenter.response.id());
    }

    @Test
    @DisplayName("Lança exceção quando restaurante não encontrado")
    void throwsWhenNotFound() {
        var id = UUID.randomUUID();
        when(restaurantDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class,
            () -> interactor.execute(new GetRestaurantByIdRequestModel(id)));
    }

    private static final class CapturingPresenter implements GetRestaurantByIdOutputBoundary {
        private GetRestaurantByIdResponseModel response;

        @Override
        public void present(GetRestaurantByIdResponseModel response) {
            this.response = response;
        }
    }
}
