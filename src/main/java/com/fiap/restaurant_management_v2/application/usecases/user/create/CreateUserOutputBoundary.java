package com.fiap.restaurant_management_v2.application.usecases.user.create;

/** Output boundary for the create-user use case, implemented by a presenter. */
public interface CreateUserOutputBoundary {
    void present(CreateUserResponseModel response);
}
