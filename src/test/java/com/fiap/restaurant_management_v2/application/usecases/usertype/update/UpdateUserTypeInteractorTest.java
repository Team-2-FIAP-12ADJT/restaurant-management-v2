package com.fiap.restaurant_management_v2.application.usecases.usertype.update;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserTypeException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserTypeInteractorTest {

    @Mock
    private UserTypeDsGateway userTypeDsGateway;

    private CapturingPresenter presenter;
    private UpdateUserTypeInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new UpdateUserTypeInteractor(userTypeDsGateway, presenter);
    }

    @Test
    @DisplayName("Atualiza tipo de usuário com sucesso")
    void updatesUserType() {
        var id = UUID.randomUUID();
        var request = new UpdateUserTypeRequestModel(id, "manager");
        when(userTypeDsGateway.findById(id))
                .thenReturn(Optional.of(new UserTypeDsResponseModel(id, "admin")));
        when(userTypeDsGateway.save(any(UserTypeDsRequestModel.class)))
                .thenAnswer(call -> {
                    UserTypeDsRequestModel ds = call.getArgument(0);
                    return new UserTypeDsResponseModel(ds.id(), ds.userType());
                });

        interactor.execute(request);

        ArgumentCaptor<UserTypeDsRequestModel> captor = ArgumentCaptor.forClass(UserTypeDsRequestModel.class);
        verify(userTypeDsGateway).save(captor.capture());
        assertEquals("manager", captor.getValue().userType());
        assertNotNull(presenter.response);
        assertEquals("manager", presenter.response.userType());
    }

    @Test
    @DisplayName("Atualiza mantendo o mesmo nome sem validar duplicidade")
    void updatesWithSameCurrentName() {
        var id = UUID.randomUUID();
        var request = new UpdateUserTypeRequestModel(id, "admin");
        when(userTypeDsGateway.findById(id))
                .thenReturn(Optional.of(new UserTypeDsResponseModel(id, "admin")));
        when(userTypeDsGateway.save(any(UserTypeDsRequestModel.class)))
                .thenAnswer(call -> {
                    UserTypeDsRequestModel ds = call.getArgument(0);
                    return new UserTypeDsResponseModel(ds.id(), ds.userType());
                });

        interactor.execute(request);

        verify(userTypeDsGateway, never()).existsByUserType(any());
        verify(userTypeDsGateway).save(any(UserTypeDsRequestModel.class));
        assertNotNull(presenter.response);
        assertEquals("admin", presenter.response.userType());
    }

    @Test
    @DisplayName("Atualização para nome ativo duplicado lança DuplicateUserTypeException")
    void rejectsDuplicateNameOnUpdate() {
        var id = UUID.randomUUID();
        var request = new UpdateUserTypeRequestModel(id, "manager");
        when(userTypeDsGateway.findById(id))
                .thenReturn(Optional.of(new UserTypeDsResponseModel(id, "admin")));
        when(userTypeDsGateway.existsByUserType("manager")).thenReturn(true);

        assertThrows(DuplicateUserTypeException.class, () -> interactor.execute(request));

        verify(userTypeDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Tipo não encontrado lança UserTypeNotFoundException")
    void notFound() {
        var id = UUID.randomUUID();
        var request = new UpdateUserTypeRequestModel(id, "manager");
        when(userTypeDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserTypeNotFoundException.class, () -> interactor.execute(request));
        verify(userTypeDsGateway, never()).save(any());
    }

    private static final class CapturingPresenter implements UpdateUserTypeOutputBoundary {
        private UpdateUserTypeResponseModel response;

        @Override
        public void present(UpdateUserTypeResponseModel response) {
            this.response = response;
        }
    }
}
