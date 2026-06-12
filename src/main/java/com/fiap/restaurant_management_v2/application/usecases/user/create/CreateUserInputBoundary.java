package com.fiap.restaurant_management_v2.application.usecases.user.create;

/**
 * Input boundary for the create-user use case. Returns void — the result flows
 * out through {@link CreateUserOutputBoundary}.
 */
public interface CreateUserInputBoundary {
    void execute(CreateUserRequestModel request);
}
