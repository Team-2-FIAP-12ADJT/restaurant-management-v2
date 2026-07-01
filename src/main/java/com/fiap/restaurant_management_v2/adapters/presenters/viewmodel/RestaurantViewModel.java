package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

public record RestaurantViewModel(
    String id,
    String name,
    String address,
    String taxIdentifier,
    String cuisineType,
    String openingHours,
    String ownerId
) {}
