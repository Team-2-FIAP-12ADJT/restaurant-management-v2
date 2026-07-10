package com.fiap.restaurant_management_v2.application.usecases.restaurant.create;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.DuplicateRestaurantException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantInteractorAdditionalTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private final TransactionalExecutor transactionalExecutor = Runnable::run;

    @Mock
    private LoggerGateway loggerGateway;

    private CreateRestaurantInteractor interactor;

    private UUID ownerId;

    @BeforeEach
    void setUp() {
        CapturingPresenter presenter = new CapturingPresenter();
        interactor = new CreateRestaurantInteractor(
            transactionalExecutor,
            restaurantDsGateway,
            userDsGateway,
                presenter,
            loggerGateway
        );
        ownerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Lança DuplicateRestaurantException quando já existe restaurante com mesmo taxIdentifier")
    void throwsWhenDuplicateTaxIdentifier() {
        var request = new CreateRestaurantRequestModel(
            "Foo",
            "Rua A",
            "12345678000199",
            "Italiana",
            "Seg-Sex 11h-23h",
            ownerId
        );

        // Simula que já existe um restaurante com o mesmo CNPJ
        when(restaurantDsGateway.findByTaxIdentifier(request.taxIdentifier()))
            .thenReturn(Optional.of(new RestaurantDsResponseModel(
                UUID.randomUUID(),
                "Existing",
                "Addr",
                request.taxIdentifier(),
                "",
                "",
                ownerId
            )));

        assertThrows(DuplicateRestaurantException.class, () ->
            interactor.execute(request)
        );

        verify(restaurantDsGateway, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private static final class CapturingPresenter implements CreateRestaurantOutputBoundary {
        @Override
        public void present(CreateRestaurantResponseModel response) {
        // Sem ação.
        }
    }
}

