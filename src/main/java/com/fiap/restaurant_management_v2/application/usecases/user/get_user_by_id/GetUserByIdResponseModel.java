package com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id;

import java.util.UUID;

/** Use-case boundary DTO pushed to the output boundary. Never carries the password. */
public record GetUserByIdResponseModel(
    UUID id,
    String name,
    String email,
    String login
) {}
