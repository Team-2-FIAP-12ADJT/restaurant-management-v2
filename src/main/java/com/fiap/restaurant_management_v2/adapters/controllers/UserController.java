package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.user.delete.DeleteUserByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.delete.DeleteUserByIdRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdRequestModel;
import java.util.UUID;

/**
 * Pure controller (no framework imports): receives raw input from the delivery
 * mechanism, packs it into the request model and invokes the input boundary.
 * Returns void — the result flows through the presenter.
 */
public class UserController {

    private final CreateUserInputBoundary createUser;
    private final GetAllUsersInputBoundary getAllUsers;
    private final GetUserByIdInputBoundary getUserById;
    private final DeleteUserByIdInputBoundary deleteUserById;

    public UserController(
        CreateUserInputBoundary createUser,
        GetAllUsersInputBoundary getAllUsers,
        GetUserByIdInputBoundary getUserById,
        DeleteUserByIdInputBoundary deleteUserById
    ) {
        this.createUser = createUser;
        this.getAllUsers = getAllUsers;
        this.getUserById = getUserById;
        this.deleteUserById = deleteUserById;
    }

    public void create(
        String name,
        String email,
        String login,
        String password
    ) {
        createUser.execute(
            new CreateUserRequestModel(name, email, login, password)
        );
    }

    public void getAll(
        String name,
        String email,
        String login,
        int page,
        int size
    ) {
        getAllUsers.execute(
            new GetAllUsersRequestModel(name, email, login, page, size)
        );
    }

    public void getById(UUID id) {
        getUserById.execute(new GetUserByIdRequestModel(id));
    }

    public void delete(UUID id) {
        deleteUserById.execute(new DeleteUserByIdRequestModel(id));
    }
}
