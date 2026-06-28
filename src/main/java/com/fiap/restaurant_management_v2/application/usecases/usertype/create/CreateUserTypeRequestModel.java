package com.fiap.restaurant_management_v2.application.usecases.usertype.create;

/** Use-case boundary DTO carrying raw input into the create-user interactor. */
public record CreateUserTypeRequestModel(
    String userType
) {}
