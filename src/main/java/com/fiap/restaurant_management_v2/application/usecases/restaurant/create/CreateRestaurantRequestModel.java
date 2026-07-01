package com.fiap.restaurant_management_v2.application.usecases.restaurant.create;

import java.util.UUID;

public record CreateRestaurantRequestModel(
    String name,
    String address,
    String taxIdentifier,
    String cuisineType,
    String openingHours,
    UUID ownerId
) {}
