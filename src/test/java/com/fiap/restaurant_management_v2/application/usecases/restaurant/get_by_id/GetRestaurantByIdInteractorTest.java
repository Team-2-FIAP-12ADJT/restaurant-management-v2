package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetRestaurantByIdInteractorTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private CapturingPresenter presenter;
    private GetRestaurantByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetRestaurantByIdInteractor(
            restaurantDsGateway,
            userDsGateway,
            presenter
        );
    }

    @Test
    @DisplayName("Retorna restaurante com owner completo quando encontrado")
    void returnsRestaurantWhenFound() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var dsResponse = new RestaurantDsResponseModel(
            id,
            "Foo",
            "Rua A",
            "12345678000199",
            "Italiana",
            "Seg-Sex 11h-23h",
            ownerId
        );
        var owner = new UserDsResponseModel(
            ownerId,
            "Dona Ada",
            "dona@example.com",
            "dona",
            "12345678901"
        );

        when(restaurantDsGateway.findById(id)).thenReturn(
            Optional.of(dsResponse)
        );
        when(userDsGateway.findById(ownerId)).thenReturn(Optional.of(owner));

        interactor.execute(new GetRestaurantByIdRequestModel(id));

        assertNotNull(presenter.response);
        assertEquals("Foo", presenter.response.name());
        assertEquals(id, presenter.response.id());
        assertEquals(owner, presenter.response.owner());
    }

    @Test
    @DisplayName("Lança exceção quando restaurante não encontrado")
    void throwsWhenNotFound() {
        var id = UUID.randomUUID();
        when(restaurantDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () ->
            interactor.execute(new GetRestaurantByIdRequestModel(id))
        );
    }

    private static final class CapturingPresenter
        implements GetRestaurantByIdOutputBoundary
    {

        private GetRestaurantByIdResponseModel response;

        @Override
        public void present(GetRestaurantByIdResponseModel response) {
            this.response = response;
        }
    }
}
