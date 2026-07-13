package com.fiap.restaurant_management_v2.application.usecases.usertype.delete;

import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserTypeByIdInteractorTest {

    @Mock
    private UserTypeDsGateway userTypeDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    @Mock
    private RestaurantDsGateway restaurantDsGateway;

    @Mock
    private TransactionalExecutor transactionalExecutor;

    @Mock
    private LoggerGateway loggerGateway;

    private DeleteUserTypeByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        CapturingPresenter presenter = new CapturingPresenter();
        interactor = new DeleteUserTypeByIdInteractor(
                userTypeDsGateway,
                userDsGateway,
                restaurantDsGateway,
                transactionalExecutor,
                presenter,
                loggerGateway
        );
    }

    @Test
    @DisplayName("Exclui tipo com sucesso e desvincula dos usuários")
    void deletesUserType() {
        var id = UUID.randomUUID();
        var request = new DeleteUserTypeByIdRequestModel(id);
        when(userTypeDsGateway.findById(id))
                .thenReturn(Optional.of(new UserTypeDsResponseModel(id, "admin")));
        when(userDsGateway.findActiveIdsByUserTypeId(id))
                .thenReturn(List.of());
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(transactionalExecutor).execute(any(Runnable.class));

        interactor.execute(request);

        verify(transactionalExecutor).execute(any(Runnable.class));
        verify(userDsGateway).unbindUserType(id);
        verify(userTypeDsGateway).deleteById(id);
    }

    @Test
    @DisplayName("Tipo não encontrado lança UserTypeNotFoundException")
    void notFound() {
        var id = UUID.randomUUID();
        var request = new DeleteUserTypeByIdRequestModel(id);
        when(userTypeDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserTypeNotFoundException.class, () -> interactor.execute(request));
        verify(transactionalExecutor, never()).execute(any(Runnable.class));
        verify(userDsGateway, never()).unbindUserType(any());
        verify(userTypeDsGateway, never()).deleteById(any());
    }

    private static final class CapturingPresenter implements DeleteUserTypeByIdOutputBoundary {
        @Override
        public void present() {
        }
    }
}
