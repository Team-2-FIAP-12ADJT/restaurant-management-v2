package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetRestaurantByIdInteractorAdditionalTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private GetRestaurantByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        CapturingPresenter presenter = new CapturingPresenter();
        interactor = new GetRestaurantByIdInteractor(
            restaurantDsGateway,
            userDsGateway,
                presenter
        );
    }

    @Test
    @DisplayName("Lança UserNotFoundException quando owner não encontrado")
    void throwsWhenOwnerMissing() {
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

        when(restaurantDsGateway.findById(id)).thenReturn(Optional.of(dsResponse));
        when(userDsGateway.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            interactor.execute(new GetRestaurantByIdRequestModel(id))
        );
    }

    private static final class CapturingPresenter implements GetRestaurantByIdOutputBoundary {
        @Override
        public void present(GetRestaurantByIdResponseModel response) {
        // Sem ação.
        }
    }
}

