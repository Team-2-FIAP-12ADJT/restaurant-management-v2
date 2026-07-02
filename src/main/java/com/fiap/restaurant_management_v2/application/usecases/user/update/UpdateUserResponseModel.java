package com.fiap.restaurant_management_v2.application.usecases.user.update;

import java.util.UUID;

public record UpdateUserResponseModel(
    UUID id,
    String name,
    String email,
    String login,
    String taxIdentifier
) {}
