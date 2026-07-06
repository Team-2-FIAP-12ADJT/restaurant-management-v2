package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user.BindUserTypeToUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user.BindUserTypeToUserRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.update.UpdateUserTypeInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.usertype.delete.DeleteUserTypeByIdInputBoundary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserTypeControllerTest {

    @Mock
    private CreateUserTypeInputBoundary createInput;

    @Mock
    private GetAllUsersTypeInputBoundary getAllInput;

    @Mock
    private GetUserTypeByIdInputBoundary getByIdInput;

    @Mock
    private UpdateUserTypeInputBoundary updateInput;

    @Mock
    private BindUserTypeToUserInputBoundary bindInput;

    @Mock
    private DeleteUserTypeByIdInputBoundary deleteInput;

    @Captor
    private ArgumentCaptor<BindUserTypeToUserRequestModel> bindCaptor;

    private UserTypeController controller;

    @BeforeEach
    void setUp() {
        controller = new UserTypeController(
                createInput,
                getAllInput,
                getByIdInput,
                updateInput,
                bindInput,
                deleteInput
        );
    }

    @Test
    @DisplayName("bind delega corretamente para o input boundary")
    void bindDelegatesToInputBoundary() {
        UUID userId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();

        controller.bind(userId, typeId);

        verify(bindInput).execute(bindCaptor.capture());
        BindUserTypeToUserRequestModel captured = bindCaptor.getValue();
        assertEquals(userId, captured.userId());
        assertEquals(typeId, captured.typeId());
    }

    @Test
    @DisplayName("create delega para CreateUserTypeInputBoundary")
    void createDelegates() {
        controller.create("admin");
        // Verifica se createInput foi executado com o modelo de requisição contendo "admin".
        var captor = org.mockito.ArgumentCaptor.forClass(com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeRequestModel.class);
        verify(createInput).execute(captor.capture());
        assertEquals("admin", captor.getValue().userType());
    }

    @Test
    @DisplayName("getAll delega para GetAllUsersTypeInputBoundary")
    void getAllDelegates() {
        controller.getAll(2, 5);
        var captor = org.mockito.ArgumentCaptor.forClass(com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeRequestModel.class);
        verify(getAllInput).execute(captor.capture());
        assertEquals(2, captor.getValue().page());
        assertEquals(5, captor.getValue().size());
    }

    @Test
    @DisplayName("getById delega para GetUserTypeByIdInputBoundary")
    void getByIdDelegates() {
        UUID id = UUID.randomUUID();
        controller.getById(id);
        var captor = org.mockito.ArgumentCaptor.forClass(com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdRequestModel.class);
        verify(getByIdInput).execute(captor.capture());
        assertEquals(id, captor.getValue().id());
    }

    @Test
    @DisplayName("update delega para UpdateUserTypeInputBoundary")
    void updateDelegates() {
        UUID id = UUID.randomUUID();
        controller.update(id, "manager");
        var captor = org.mockito.ArgumentCaptor.forClass(com.fiap.restaurant_management_v2.application.usecases.usertype.update.UpdateUserTypeRequestModel.class);
        verify(updateInput).execute(captor.capture());
        assertEquals(id, captor.getValue().id());
        assertEquals("manager", captor.getValue().userType());
    }

    @Test
    @DisplayName("delete delega para DeleteUserTypeByIdInputBoundary")
    void deleteDelegates() {
        UUID id = UUID.randomUUID();
        controller.delete(id);
        var captor = org.mockito.ArgumentCaptor.forClass(com.fiap.restaurant_management_v2.application.usecases.usertype.delete.DeleteUserTypeByIdRequestModel.class);
        verify(deleteInput).execute(captor.capture());
        assertEquals(id, captor.getValue().id());
    }
}

