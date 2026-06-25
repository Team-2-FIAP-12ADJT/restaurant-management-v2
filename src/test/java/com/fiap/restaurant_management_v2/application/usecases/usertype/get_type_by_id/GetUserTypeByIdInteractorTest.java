package com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id;

import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserTypeByIdInteractorTest {

    @Mock
    private UserTypeDsGateway userTypeDsGateway;

    private CapturingPresenter presenter;
    private GetUserTypeByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetUserTypeByIdInteractor(userTypeDsGateway, presenter);
    }

    @Test
    @DisplayName("Retorna tipo de usuário por ID")
    void returnsUserType() {
        var id = UUID.randomUUID();
        var request = new GetUserTypeByIdRequestModel(id);
        when(userTypeDsGateway.findById(id))
                .thenReturn(Optional.of(new UserTypeDsResponseModel(id, "admin")));

        interactor.execute(request);

        assertNotNull(presenter.response);
        assertEquals(id, presenter.response.id());
        assertEquals("admin", presenter.response.userType());
    }

    @Test
    @DisplayName("Tipo não encontrado lança UserTypeNotFoundException")
    void notFound() {
        var id = UUID.randomUUID();
        var request = new GetUserTypeByIdRequestModel(id);
        when(userTypeDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserTypeNotFoundException.class, () -> interactor.execute(request));
    }

    private static final class CapturingPresenter implements GetUserTypeByIdOutputBoundary {
        private GetUserTypeByIdResponseModel response;

        @Override
        public void present(GetUserTypeByIdResponseModel response) {
            this.response = response;
        }
    }
}
