package com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id;

/** Use-case boundary DTO pushed to the output boundary. Never carries the password. */
public interface GetUserByIdOutputBoundary {
    void present(GetUserByIdResponseModel response);
}
