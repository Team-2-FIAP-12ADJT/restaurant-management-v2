package com.fiap.restaurant_management_v2.application.usecases.usertype.create;

/** Input boundary for the create-user-type use case. */
public interface CreateUserTypeInputBoundary {
    void execute(CreateUserTypeRequestModel request);
}
