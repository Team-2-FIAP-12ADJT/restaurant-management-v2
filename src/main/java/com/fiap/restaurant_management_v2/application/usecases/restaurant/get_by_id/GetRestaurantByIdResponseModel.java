package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id;

import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import java.util.UUID;

public record GetRestaurantByIdResponseModel(
    UUID id,
    String name,
    String address,
    String taxIdentifier,
    String cuisineType,
    String openingHours,
    UserDsResponseModel owner
) {}
