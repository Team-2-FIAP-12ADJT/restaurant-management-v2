package com.fiap.restaurant_management_v2.application.usecases.usertype.update;

import java.util.UUID;

/** Use-case boundary DTO carrying raw input into the create-user interactor. */
public record UpdateUserTypeRequestModel(
        UUID id ,
        String userType
) {}
