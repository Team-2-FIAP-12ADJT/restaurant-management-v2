package com.fiap.restaurant_management_v2.application.usecases.restaurant.update;

import java.util.UUID;

public record UpdateRestaurantRequestModel(
    UUID id,
    String name,
    String address,
    String cuisineType,
    String openingHours,
    UUID ownerId
) {}
