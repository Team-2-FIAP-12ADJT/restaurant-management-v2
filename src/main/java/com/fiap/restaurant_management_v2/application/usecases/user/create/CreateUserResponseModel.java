package com.fiap.restaurant_management_v2.application.usecases.user.create;

import java.util.UUID;

/** Use-case boundary DTO pushed to the output boundary. Never carries the password. */
public record CreateUserResponseModel(
    UUID id,
    String name,
    String email,
    String login
) {}
