package com.fiap.restaurant_management_v2.application.usecases.user.create;

/** Use-case boundary DTO carrying raw input into the create-user interactor. */
public record CreateUserRequestModel(
    String name,
    String email,
    String login,
    String password
) {}
