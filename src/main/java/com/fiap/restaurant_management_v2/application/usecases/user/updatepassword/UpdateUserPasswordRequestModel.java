package com.fiap.restaurant_management_v2.application.usecases.user.updatepassword;

import java.util.UUID;

/** Use-case boundary DTO carrying raw input into the update-password interactor. */
public record UpdateUserPasswordRequestModel(
    UUID id,
    String oldPassword,
    String newPassword
) {}
