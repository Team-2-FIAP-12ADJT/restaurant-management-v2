package com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user;

import java.util.UUID;

/** Use-case boundary DTO carrying raw input into the create-user interactor. */
public record BindUserTypeToUserRequestModel(
        UUID userId ,
        UUID typeId
) {}
