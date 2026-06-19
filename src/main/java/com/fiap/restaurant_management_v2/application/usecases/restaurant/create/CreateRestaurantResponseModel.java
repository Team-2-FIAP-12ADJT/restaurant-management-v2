package com.fiap.restaurant_management_v2.application.usecases.restaurant.create;

import java.util.UUID;

public record CreateRestaurantResponseModel(
    UUID id,
    String name,
    String address,
    String cuisineType,
    String openingHours,
    UUID ownerId
) {}
