package com.fiap.restaurant_management_v2.application.usecases.restaurant.get_all;

public record GetAllRestaurantsRequestModel(
    String name,
    String cuisineType,
    int page,
    int size
) {}
