package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersRequestModel;

/**
 * Pure controller (no framework imports): receives raw input from the delivery
 * mechanism, packs it into the request model and invokes the input boundary.
 * Returns void — the result flows through the presenter.
 */
public class UserController {

    private final CreateUserInputBoundary createUser;
    private final GetAllUsersInputBoundary getAllUsers;

    public UserController(
        CreateUserInputBoundary createUser,
        GetAllUsersInputBoundary getAllUsers
    ) {
        this.createUser = createUser;
        this.getAllUsers = getAllUsers;
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
}
