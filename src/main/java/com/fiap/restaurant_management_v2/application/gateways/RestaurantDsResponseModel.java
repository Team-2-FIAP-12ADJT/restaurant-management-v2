package com.fiap.restaurant_management_v2.application.gateways;

import java.util.UUID;

public record RestaurantDsResponseModel(
    UUID id,
    String name,
    String address,
    String cuisineType,
    String openingHours,
    UUID ownerId
) {}
