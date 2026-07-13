package com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserByIdInteractorTest {

    @Mock
    private UserDsGateway userDsGateway;

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    private CapturingPresenter presenter;
    private GetUserByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetUserByIdInteractor(userDsGateway, restaurantDsGateway, presenter);
    }

    @Test
    @DisplayName("Retorna usuário quando encontrado")
    void returnsUserWhenFound() {
        var id = UUID.randomUUID();
        var userTypeId = UUID.randomUUID();
        var user = new UserDsResponseModel(
            id,
            "Foo",
            "foo@example.com",
            "foo",
            "123456789",
            userTypeId,
            "DONO"
        );

        when(userDsGateway.findById(id)).thenReturn(Optional.of(user));
        when(restaurantDsGateway.findAllByOwnerIds(List.of(id))).thenReturn(List.of());

        interactor.execute(new GetUserByIdRequestModel(id));

        assertNotNull(presenter.response);
        assertEquals(id, presenter.response.id());
        assertEquals("Foo", presenter.response.name());
        assertEquals("foo@example.com", presenter.response.email());
        assertEquals("foo", presenter.response.login());
        assertEquals("DONO", presenter.response.userTypeName());
        assertEquals(List.of(), presenter.response.restaurants());
    }

    @Test
    @DisplayName("Lança exceção quando usuário não encontrado")
    void throwsWhenNotFound() {
        var id = UUID.randomUUID();

        when(userDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            interactor.execute(new GetUserByIdRequestModel(id))
        );
    }

    private static final class CapturingPresenter
        implements GetUserByIdOutputBoundary
    {

        private GetUserByIdResponseModel response;

        @Override
        public void present(GetUserByIdResponseModel response) {
            this.response = response;
        }
    }
}
