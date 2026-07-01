package com.fiap.restaurant_management_v2.application.usecases.restaurant.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.domain.exception.InvalidRestaurantException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantInteractorTest {

    private static final String CNPJ = "12345678000199";

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private final TransactionalExecutor transactionalExecutor = Runnable::run;

    private CapturingPresenter presenter;
    private CreateRestaurantInteractor interactor;

    private UUID ownerId;
    private UserDsResponseModel owner;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new CreateRestaurantInteractor(
            transactionalExecutor,
            restaurantDsGateway,
            userDsGateway,
            presenter
        );
        ownerId = UUID.randomUUID();
        owner = new UserDsResponseModel(
            ownerId,
            "Dona Ada",
            "dona@example.com",
            "dona",
            "12345678901"
        );
    }

    @Test
    @DisplayName("Cadastra restaurante com sucesso e retorna owner completo")
    void createsRestaurantSuccessfully() {
        var request = new CreateRestaurantRequestModel(
            "Foo",
            "Rua A",
            CNPJ,
            "Italiana",
            "Seg-Sex 11h-23h",
            ownerId
        );

        when(restaurantDsGateway.findByTaxIdentifier(CNPJ)).thenReturn(
            Optional.empty()
        );
        when(userDsGateway.findById(ownerId)).thenReturn(Optional.of(owner));
        when(
            restaurantDsGateway.save(any(RestaurantDsRequestModel.class))
        ).thenAnswer(call -> {
            RestaurantDsRequestModel ds = call.getArgument(0);
            return new RestaurantDsResponseModel(
                ds.id(),
                ds.name(),
                ds.address(),
                ds.taxIdentifier(),
                ds.cuisineType(),
                ds.openingHours(),
                ds.ownerId()
            );
        });

        interactor.execute(request);

        ArgumentCaptor<RestaurantDsRequestModel> captor =
            ArgumentCaptor.forClass(RestaurantDsRequestModel.class);
        verify(restaurantDsGateway).save(captor.capture());
        assertEquals("Foo", captor.getValue().name());
        assertEquals(CNPJ, captor.getValue().taxIdentifier());
        assertEquals(ownerId, captor.getValue().ownerId());

        assertNotNull(presenter.response);
        assertEquals("Foo", presenter.response.name());
        assertEquals(owner, presenter.response.owner());
    }

    @Test
    @DisplayName("Owner inexistente → UserNotFoundException; não persiste")
    void rejectsInvalidOwner() {
        var request = new CreateRestaurantRequestModel(
            "Foo",
            "Rua A",
            CNPJ,
            "Italiana",
            "Seg-Sex 11h-23h",
            ownerId
        );

        when(restaurantDsGateway.findByTaxIdentifier(CNPJ)).thenReturn(
            Optional.empty()
        );
        when(userDsGateway.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            interactor.execute(request)
        );
        verify(restaurantDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Nome em branco → InvalidRestaurantException; não persiste")
    void rejectsBlankName() {
        var request = new CreateRestaurantRequestModel(
            "",
            "Rua A",
            CNPJ,
            "Italiana",
            "Seg-Sex 11h-23h",
            ownerId
        );

        when(restaurantDsGateway.findByTaxIdentifier(CNPJ)).thenReturn(
            Optional.empty()
        );
        when(userDsGateway.findById(ownerId)).thenReturn(Optional.of(owner));

        assertThrows(InvalidRestaurantException.class, () ->
            interactor.execute(request)
        );
        verify(restaurantDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    private static final class CapturingPresenter
        implements CreateRestaurantOutputBoundary
    {

        private CreateRestaurantResponseModel response;

        @Override
        public void present(CreateRestaurantResponseModel response) {
            this.response = response;
        }
    }
}
