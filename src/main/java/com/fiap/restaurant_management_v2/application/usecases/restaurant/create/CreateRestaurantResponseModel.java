package com.fiap.restaurant_management_v2.application.usecases.restaurant.create;

import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import java.util.UUID;

public record CreateRestaurantResponseModel(
    UUID id,
    String name,
    String address,
    String taxIdentifier,
    String cuisineType,
    String openingHours,
    UserDsResponseModel owner
) {}
