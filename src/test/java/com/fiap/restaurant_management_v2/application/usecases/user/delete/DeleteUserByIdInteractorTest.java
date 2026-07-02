package com.fiap.restaurant_management_v2.application.usecases.user.delete;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.UserHasActiveRestaurantsException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
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
class DeleteUserByIdInteractorTest {

    @Mock
    private UserDsGateway userDsGateway;

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    private CapturingPresenter presenter;
    private DeleteUserByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new DeleteUserByIdInteractor(
            userDsGateway,
            restaurantDsGateway,
            presenter
        );
    }

    @Test
    @DisplayName("Exclui usuário com sucesso quando não possui restaurante ativo")
    void deletesSuccessfully() {
        var id = UUID.randomUUID();
        var user = new UserDsResponseModel(
            id,
            "Foo",
            "foo@example.com",
            "foo",
            "12345678900"
        );

        when(userDsGateway.findById(id)).thenReturn(Optional.of(user));
        when(restaurantDsGateway.existsByOwnerIdAndIsActive(id)).thenReturn(
            false
        );

        interactor.execute(new DeleteUserByIdRequestModel(id));

        verify(userDsGateway).deleteById(id);
        assertTrue(presenter.presented);
    }

    @Test
    @DisplayName("Usuário inexistente lança exceção e não deleta")
    void throwsWhenNotFound() {
        var id = UUID.randomUUID();

        when(userDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            interactor.execute(new DeleteUserByIdRequestModel(id))
        );

        verify(userDsGateway, never()).deleteById(any());
    }

    @Test
    @DisplayName(
        "Usuário com restaurante ativo → UserHasActiveRestaurantsException; não deleta"
    )
    void throwsWhenUserHasActiveRestaurants() {
        var id = UUID.randomUUID();
        var user = new UserDsResponseModel(
            id,
            "Foo",
            "foo@example.com",
            "foo",
            "12345678900"
        );

        when(userDsGateway.findById(id)).thenReturn(Optional.of(user));
        when(restaurantDsGateway.existsByOwnerIdAndIsActive(id)).thenReturn(
            true
        );

        assertThrows(UserHasActiveRestaurantsException.class, () ->
            interactor.execute(new DeleteUserByIdRequestModel(id))
        );

        verify(userDsGateway, never()).deleteById(any());
    }

    private static final class CapturingPresenter
        implements DeleteUserByIdOutputBoundary
    {

        private boolean presented;

        @Override
        public void present() {
            this.presented = true;
        }
    }
}
