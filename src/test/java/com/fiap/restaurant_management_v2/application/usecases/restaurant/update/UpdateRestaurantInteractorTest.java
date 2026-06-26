package com.fiap.restaurant_management_v2.application.usecases.restaurant.update;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
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
class UpdateRestaurantInteractorTest {

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private CapturingPresenter presenter;
    private UpdateRestaurantInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new UpdateRestaurantInteractor(restaurantDsGateway, userDsGateway, presenter);
    }

    @Test
    @DisplayName("Atualiza restaurante com sucesso")
    void updatesSuccessfully() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var request = new UpdateRestaurantRequestModel(id, "Bar", "Rua B", "Japonesa", "Seg-Sab 12h-22h", ownerId);

        when(restaurantDsGateway.existsById(id)).thenReturn(true);
        when(userDsGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantDsGateway.save(any(RestaurantDsRequestModel.class))).thenAnswer(call -> {
            RestaurantDsRequestModel ds = call.getArgument(0);
            return new RestaurantDsResponseModel(ds.id(), ds.name(), ds.address(), ds.cuisineType(), ds.openingHours(), ds.ownerId());
        });

        interactor.execute(request);

        ArgumentCaptor<RestaurantDsRequestModel> captor = ArgumentCaptor.forClass(RestaurantDsRequestModel.class);
        verify(restaurantDsGateway).save(captor.capture());
        assertEquals("Bar", captor.getValue().name());
        assertEquals(id, captor.getValue().id());

        assertNotNull(presenter.response);
        assertEquals("Bar", presenter.response.name());
    }

    @Test
    @DisplayName("Restaurante inexistente lança exceção")
    void throwsWhenNotFound() {
        var id = UUID.randomUUID();
        var request = new UpdateRestaurantRequestModel(id, "Bar", "Rua B", "Japonesa", "Seg-Sab 12h-22h", UUID.randomUUID());

        when(restaurantDsGateway.existsById(id)).thenReturn(false);

        assertThrows(RestaurantNotFoundException.class, () -> interactor.execute(request));
        verify(restaurantDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Owner inválido lança exceção")
    void throwsWhenOwnerNotFound() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var request = new UpdateRestaurantRequestModel(id, "Bar", "Rua B", "Japonesa", "Seg-Sab 12h-22h", ownerId);

        when(restaurantDsGateway.existsById(id)).thenReturn(true);
        when(userDsGateway.existsById(ownerId)).thenReturn(false);

        assertThrows(DuplicateUserException.class, () -> interactor.execute(request));
        verify(restaurantDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    private static final class CapturingPresenter implements UpdateRestaurantOutputBoundary {
        private UpdateRestaurantResponseModel response;

        @Override
        public void present(UpdateRestaurantResponseModel response) {
            this.response = response;
        }
    }
}
