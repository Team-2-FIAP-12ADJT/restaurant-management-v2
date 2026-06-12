package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserRequestModel;

/**
 * Pure controller (no framework imports): receives raw input from the delivery
 * mechanism, packs it into the request model and invokes the input boundary.
 * Returns void — the result flows through the presenter.
 */
public class UserController {

    private final CreateUserInputBoundary createUser;

    public UserController(CreateUserInputBoundary createUser) {
        this.createUser = createUser;
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
}
