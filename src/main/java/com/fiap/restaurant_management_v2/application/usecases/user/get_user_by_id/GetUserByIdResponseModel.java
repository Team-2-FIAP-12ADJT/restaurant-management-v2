package com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import java.util.List;
import java.util.UUID;

/** Use-case boundary DTO pushed to the output boundary. Never carries the password. */
public record GetUserByIdResponseModel(
    UUID id,
    String name,
    String email,
    String login,
    String taxIdentifier,
    String userTypeName,
    List<RestaurantDsResponseModel> restaurants
) {}
