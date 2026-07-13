package com.fiap.restaurant_management_v2.application.usecases.user.get_all;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import java.util.List;
import java.util.UUID;

public record UserSummary(
    UUID id,
    String name,
    String email,
    String login,
    String taxIdentifier,
    String userTypeName,
    List<RestaurantDsResponseModel> restaurants
) {}
