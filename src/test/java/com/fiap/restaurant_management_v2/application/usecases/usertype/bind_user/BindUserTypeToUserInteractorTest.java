package com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.exception.UserTypeNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.UserBindDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BindUserTypeToUserInteractorTest {

    @Mock
    private UserTypeDsGateway userTypeDsGateway;

    @Mock
    private UserDsGateway userDsGateway;

    private BindUserTypeToUserInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new BindUserTypeToUserInteractor(userTypeDsGateway, userDsGateway);
    }

    @Test
    @DisplayName("Vincula tipo de usuário com sucesso")
    void bindsSuccessfully() {
        var userId = UUID.randomUUID();
        var typeId = UUID.randomUUID();
        var request = new BindUserTypeToUserRequestModel(userId, typeId);

        when(userDsGateway.findAllById(userId))
                .thenReturn(Optional.of(new UserBindDsResponseModel(userId, "Ada", "ada@test.com", "ada", "pass")));
        when(userTypeDsGateway.findById(typeId))
                .thenReturn(Optional.of(new UserTypeDsResponseModel(typeId, "admin")));

        interactor.execute(request);

        verify(userDsGateway).bindUserType(userId, typeId);
    }

    @Test
    @DisplayName("Usuário não encontrado lança UserNotFoundException")
    void userNotFound() {
        var userId = UUID.randomUUID();
        var typeId = UUID.randomUUID();
        var request = new BindUserTypeToUserRequestModel(userId, typeId);

        when(userDsGateway.findAllById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> interactor.execute(request));
        verify(userDsGateway, never()).bindUserType(any(), any());
    }

    @Test
    @DisplayName("Tipo não encontrado lança UserTypeNotFoundException")
    void typeNotFound() {
        var userId = UUID.randomUUID();
        var typeId = UUID.randomUUID();
        var request = new BindUserTypeToUserRequestModel(userId, typeId);

        when(userDsGateway.findAllById(userId))
                .thenReturn(Optional.of(new UserBindDsResponseModel(userId, "Ada", "ada@test.com", "ada", "pass")));
        when(userTypeDsGateway.findById(typeId)).thenReturn(Optional.empty());

        assertThrows(UserTypeNotFoundException.class, () -> interactor.execute(request));
        verify(userDsGateway, never()).bindUserType(any(), any());
    }
}
