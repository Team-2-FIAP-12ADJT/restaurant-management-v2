package com.fiap.restaurant_management_v2.application.usecases.usertype.create;

/** Output boundary for the create-user-type use case, implemented by a presenter. */
public interface CreateUserTypeOutputBoundary {
    void present(CreateUserTypeResponseModel response);
}
