package com.fiap.restaurant_management_v2.application.usecases.usertype.update;

import java.util.UUID;

/** Use-case boundary DTO pushed to the output boundary. Never carries the password. */
public record UpdateUserTypeResponseModel(
    UUID id ,
    String userType
) {}
