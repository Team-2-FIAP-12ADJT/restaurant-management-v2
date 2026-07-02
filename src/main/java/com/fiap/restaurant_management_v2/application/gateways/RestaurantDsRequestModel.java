package com.fiap.restaurant_management_v2.application.gateways;

import java.util.UUID;

public record RestaurantDsRequestModel(
    UUID id,
    String name,
    String address,
    String taxIdentifier,
    String cuisineType,
    String openingHours,
    UUID ownerId
) {}
