package com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id;

/** Use-case boundary DTO pushed to the output boundary. Never carries the password. */
public interface GetUserTypeByIdOutputBoundary {
    void present(GetUserTypeByIdResponseModel response);
}
