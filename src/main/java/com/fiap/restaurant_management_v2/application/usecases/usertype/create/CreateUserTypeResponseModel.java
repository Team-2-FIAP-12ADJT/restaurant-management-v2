package com.fiap.restaurant_management_v2.application.usecases.usertype.create;

import java.util.UUID;

/** Use-case boundary DTO pushed to the output boundary. Never carries the password. */
public record CreateUserTypeResponseModel(
    UUID id,
    String userType
) {}
