package com.fiap.restaurant_management_v2.application.usecases.usertype.create;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserTypeException;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserTypeInteractorTest {

    @Mock
    private UserTypeDsGateway userTypeDsGateway;

    private CapturingPresenter presenter;
    private CreateUserTypeInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new CreateUserTypeInteractor(userTypeDsGateway, presenter);
    }

    @Test
    @DisplayName("Cria tipo de usuário com sucesso")
    void createsUserType() {
        var request = new CreateUserTypeRequestModel("admin");
        when(userTypeDsGateway.existsByUserType("admin")).thenReturn(false);
        when(userTypeDsGateway.save(any(UserTypeDsRequestModel.class)))
                .thenAnswer(call -> {
                    UserTypeDsRequestModel ds = call.getArgument(0);
                    return new UserTypeDsResponseModel(ds.id(), ds.userType());
                });

        interactor.execute(request);

        verify(userTypeDsGateway).save(any());
        assertNotNull(presenter.response);
        assertNotNull(presenter.response.id());
        assertEquals("admin", presenter.response.userType());
    }

    @Test
    @DisplayName("Tipo duplicado lança DuplicateUserTypeException")
    void rejectsDuplicate() {
        var request = new CreateUserTypeRequestModel("admin");
        when(userTypeDsGateway.existsByUserType("admin")).thenReturn(true);

        assertThrows(DuplicateUserTypeException.class, () -> interactor.execute(request));
        verify(userTypeDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    private static final class CapturingPresenter implements CreateUserTypeOutputBoundary {
        private CreateUserTypeResponseModel response;

        @Override
        public void present(CreateUserTypeResponseModel response) {
            this.response = response;
        }
    }
}
