package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_by_id;

import java.util.UUID;

public record GetRestaurantByIdResponseModel(
    UUID id,
    String name,
    String address,
    String cuisineType,
    String openingHours,
    UUID ownerId
) {}
