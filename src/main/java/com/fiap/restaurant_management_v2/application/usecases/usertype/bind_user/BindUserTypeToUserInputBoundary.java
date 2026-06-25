package com.fiap.restaurant_management_v2.application.usecases.usertype.bind_user;

/** Input boundary for the bind-user-type use case. */
public interface BindUserTypeToUserInputBoundary {
    void execute(BindUserTypeToUserRequestModel request);
}
