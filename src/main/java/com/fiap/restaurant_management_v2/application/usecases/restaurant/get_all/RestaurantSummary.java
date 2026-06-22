package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all;

import java.util.UUID;

public record RestaurantSummary(
    UUID id,
    String name,
    String address,
    String cuisineType,
    String openingHours,
    UUID ownerId
) {}
