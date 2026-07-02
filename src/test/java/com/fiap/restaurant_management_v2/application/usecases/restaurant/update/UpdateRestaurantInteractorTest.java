package com.fiap.restaurant_management_v2.application.usecases.restaurant.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.DuplicateRestaurantException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
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
class UpdateRestaurantInteractorTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    // Executor real que só roda a ação (espelha o boundary @Transactional).
    private final TransactionalExecutor transactionalExecutor = Runnable::run;

    private CapturingPresenter presenter;
    private UpdateRestaurantInteractor interactor;

    private UUID id;
    private UUID ownerId;
    private RestaurantDsResponseModel current;
    private UserDsResponseModel owner;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new UpdateRestaurantInteractor(
            transactionalExecutor,
            restaurantDsGateway,
            userDsGateway,
            presenter
        );
        id = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        current = new RestaurantDsResponseModel(
            id,
            "Bar Velho",
            "Rua A",
            "12345678000199",
            "Japonesa",
            "Seg-Sex 12h-22h",
            ownerId
        );
        owner = new UserDsResponseModel(
            ownerId,
            "Dona Ada",
            "dona@example.com",
            "dona",
            "12345678901"
        );
    }

    @Test
    @DisplayName(
        "PATCH parcial sem ownerId mantém o owner atual (não dá 404) e retorna o owner completo"
    )
    void partialUpdateKeepsOwnerAndReturnsFullOwner() {
        when(restaurantDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(userDsGateway.findById(ownerId)).thenReturn(Optional.of(owner));
        var saved = new RestaurantDsResponseModel(
            id,
            "Bar Novo",
            "Rua A",
            "12345678000199",
            "Japonesa",
            "Seg-Sex 12h-22h",
            ownerId
        );
        when(
            restaurantDsGateway.update(
                id,
                "Bar Novo",
                "Rua A",
                "12345678000199",
                "Japonesa",
                "Seg-Sex 12h-22h",
                ownerId
            )
        ).thenReturn(saved);

        // só name muda; ownerId null = mantém o current
        interactor.execute(
            new UpdateRestaurantRequestModel(
                id,
                "Bar Novo",
                null,
                null,
                null,
                null,
                null
            )
        );

        verify(restaurantDsGateway).update(
            id,
            "Bar Novo",
            "Rua A",
            "12345678000199",
            "Japonesa",
            "Seg-Sex 12h-22h",
            ownerId
        );
        assertNotNull(presenter.response);
        assertEquals("Bar Novo", presenter.response.name());
        // owner completo na resposta (não só id)
        assertEquals(owner, presenter.response.owner());
    }

    @Test
    @DisplayName("Restaurante inexistente → RestaurantNotFoundException; não persiste")
    void throwsWhenRestaurantNotFound() {
        when(restaurantDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () ->
            interactor.execute(
                new UpdateRestaurantRequestModel(
                    id,
                    "X",
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        );

        verify(restaurantDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    @Test
    @DisplayName("Owner (efetivo) inexistente → UserNotFoundException; não persiste")
    void throwsWhenOwnerNotFound() {
        UUID newOwnerId = UUID.randomUUID();
        when(restaurantDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(userDsGateway.findById(newOwnerId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            interactor.execute(
                new UpdateRestaurantRequestModel(
                    id,
                    null,
                    null,
                    null,
                    null,
                    null,
                    newOwnerId
                )
            )
        );

        verify(restaurantDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    @Test
    @DisplayName("Troca de owner persiste o novo ownerId e retorna o novo owner completo")
    void changesOwnerPersistsAndReturnsNewOwner() {
        UUID newOwnerId = UUID.randomUUID();
        var newOwner = new UserDsResponseModel(
            newOwnerId,
            "Novo Dono",
            "novo@example.com",
            "novo",
            "98765432100"
        );
        when(restaurantDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(userDsGateway.findById(newOwnerId)).thenReturn(
            Optional.of(newOwner)
        );
        var saved = new RestaurantDsResponseModel(
            id,
            "Bar Velho",
            "Rua A",
            "12345678000199",
            "Japonesa",
            "Seg-Sex 12h-22h",
            newOwnerId
        );
        when(
            restaurantDsGateway.update(
                id,
                "Bar Velho",
                "Rua A",
                "12345678000199",
                "Japonesa",
                "Seg-Sex 12h-22h",
                newOwnerId
            )
        ).thenReturn(saved);

        interactor.execute(
            new UpdateRestaurantRequestModel(
                id,
                null,
                null,
                null,
                null,
                null,
                newOwnerId
            )
        );

        verify(restaurantDsGateway).update(
            eq(id),
            any(),
            any(),
            any(),
            any(),
            any(),
            eq(newOwnerId)
        );
        assertEquals(newOwner, presenter.response.owner());
    }

    @Test
    @DisplayName("CNPJ novo já em uso (excluindo self) → DuplicateRestaurantException")
    void throwsWhenCnpjDuplicated() {
        when(restaurantDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(userDsGateway.findById(ownerId)).thenReturn(Optional.of(owner));
        when(
            restaurantDsGateway.existsByCnpjExcludingId("99999999000199", id)
        ).thenReturn(true);

        assertThrows(DuplicateRestaurantException.class, () ->
            interactor.execute(
                new UpdateRestaurantRequestModel(
                    id,
                    null,
                    null,
                    "99999999000199",
                    null,
                    null,
                    null
                )
            )
        );

        verify(restaurantDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    private static final class CapturingPresenter
        implements UpdateRestaurantOutputBoundary
    {

        private UpdateRestaurantResponseModel response;

        @Override
        public void present(UpdateRestaurantResponseModel response) {
            this.response = response;
        }
    }
}
