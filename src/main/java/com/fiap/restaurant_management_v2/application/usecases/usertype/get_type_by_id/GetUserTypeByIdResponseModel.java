package com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id;

import java.util.UUID;

public record GetUserTypeByIdResponseModel(
    UUID id,
    String userType
) {}
