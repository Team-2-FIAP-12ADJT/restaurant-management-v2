package com.fiap.restaurant_management_v2.adapters.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.user.delete.DeleteUserByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.delete.DeleteUserByIdRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.user.update.UpdateUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.update.UpdateUserRequestModel;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private CreateUserInputBoundary createUser;

    @Mock
    private GetAllUsersInputBoundary getAllUsers;

    @Mock
    private GetUserByIdInputBoundary getUserById;

    @Mock
    private DeleteUserByIdInputBoundary deleteUserById;

    @Mock
    private UpdateUserInputBoundary updateUser;

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(
            createUser,
            getAllUsers,
            getUserById,
            deleteUserById,
            updateUser
        );
    }

    @Test
    void createDelegatesRequestModel() {
        controller.create("Ada", "ada@example.com", "ada", "12345678901", "secret");

        var captor = ArgumentCaptor.forClass(CreateUserRequestModel.class);
        verify(createUser).execute(captor.capture());
        assertEquals("Ada", captor.getValue().name());
        assertEquals("ada@example.com", captor.getValue().email());
        assertEquals("ada", captor.getValue().login());
        assertEquals("12345678901", captor.getValue().taxIdentifier());
        assertEquals("secret", captor.getValue().password());
    }

    @Test
    void getAllDelegatesRequestModel() {
        controller.getAll("Ada", "ada@example.com", "ada", "12345678901", 2, 20);

        var captor = ArgumentCaptor.forClass(GetAllUsersRequestModel.class);
        verify(getAllUsers).execute(captor.capture());
        assertEquals("Ada", captor.getValue().name());
        assertEquals("ada@example.com", captor.getValue().email());
        assertEquals("ada", captor.getValue().login());
        assertEquals("12345678901", captor.getValue().taxIdentifier());
        assertEquals(2, captor.getValue().page());
        assertEquals(20, captor.getValue().size());
    }

    @Test
    void getByIdDelegatesRequestModel() {
        UUID id = UUID.randomUUID();

        controller.getById(id);

        var captor = ArgumentCaptor.forClass(GetUserByIdRequestModel.class);
        verify(getUserById).execute(captor.capture());
        assertEquals(id, captor.getValue().id());
    }

    @Test
    void deleteDelegatesRequestModel() {
        UUID id = UUID.randomUUID();

        controller.delete(id);

        var captor = ArgumentCaptor.forClass(DeleteUserByIdRequestModel.class);
        verify(deleteUserById).execute(captor.capture());
        assertEquals(id, captor.getValue().id());
    }

    @Test
    void updateDelegatesRequestModel() {
        UUID id = UUID.randomUUID();

        controller.update(id, "Ada", "ada@example.com", "ada", "12345678901");

        var captor = ArgumentCaptor.forClass(UpdateUserRequestModel.class);
        verify(updateUser).execute(captor.capture());
        assertEquals(id, captor.getValue().id());
        assertEquals("Ada", captor.getValue().name());
        assertEquals("ada@example.com", captor.getValue().email());
        assertEquals("ada", captor.getValue().login());
        assertEquals("12345678901", captor.getValue().taxIdentifier());
    }
}
