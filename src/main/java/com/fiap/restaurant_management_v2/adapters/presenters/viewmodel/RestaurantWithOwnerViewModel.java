package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

/**
 * View-ready restaurant data with the full owner nested (id já como String).
 * Usado só pela rota de update — create/get/get-all expõem apenas {@code ownerId}
 * via {@link RestaurantViewModel}.
 */
public record RestaurantWithOwnerViewModel(
    String id,
    String name,
    String address,
    String taxIdentifier,
    String cuisineType,
    String openingHours,
    UserViewModel owner
) {}
