package com.fiap.restaurant_management_v2.application.usecases.restaurant.create;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantInteractorTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private CapturingPresenter presenter;
    private CreateRestaurantInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new CreateRestaurantInteractor(restaurantDsGateway, userDsGateway, presenter);
    }

    @Test
    @DisplayName("Cadastra restaurante com sucesso e owner válido")
    void createsRestaurantSuccessfully() {
        var ownerId = UUID.randomUUID();
        var request = new CreateRestaurantRequestModel("Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);

        when(userDsGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantDsGateway.save(any(RestaurantDsRequestModel.class))).thenAnswer(call -> {
            RestaurantDsRequestModel ds = call.getArgument(0);
            return new RestaurantDsResponseModel(ds.id(), ds.name(), ds.address(), ds.cuisineType(), ds.openingHours(), ds.ownerId());
        });

        interactor.execute(request);

        ArgumentCaptor<RestaurantDsRequestModel> captor = ArgumentCaptor.forClass(RestaurantDsRequestModel.class);
        verify(restaurantDsGateway).save(captor.capture());
        assertEquals("Foo", captor.getValue().name());
        assertEquals(ownerId, captor.getValue().ownerId());

        assertNotNull(presenter.response);
        assertNotNull(presenter.response.id());
        assertEquals("Foo", presenter.response.name());
    }

    @Test
    @DisplayName("Owner inválido lança exceção")
    void rejectsInvalidOwner() {
        var ownerId = UUID.randomUUID();
        var request = new CreateRestaurantRequestModel("Foo", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);

        when(userDsGateway.existsById(ownerId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> interactor.execute(request));
        verify(restaurantDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Dados inválidos (nome em branco) não persistem")
    void rejectsBlankName() {
        var ownerId = UUID.randomUUID();
        var request = new CreateRestaurantRequestModel("", "Rua A", "Italiana", "Seg-Sex 11h-23h", ownerId);

        when(userDsGateway.existsById(ownerId)).thenReturn(true);

        assertThrows(Exception.class, () -> interactor.execute(request));
        verify(restaurantDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    private static final class CapturingPresenter implements CreateRestaurantOutputBoundary {
        private CreateRestaurantResponseModel response;

        @Override
        public void present(CreateRestaurantResponseModel response) {
            this.response = response;
        }
    }
}
