package com.fiap.restaurant_management_v2.application.usecases.user.updatepassword;

/**
 * Input boundary for the update-password use case. Returns void — the result flows
 * out through {@link UpdateUserPasswordOutputBoundary}.
 */
public interface UpdateUserPasswordInputBoundary {
    void execute(UpdateUserPasswordRequestModel request);
}
