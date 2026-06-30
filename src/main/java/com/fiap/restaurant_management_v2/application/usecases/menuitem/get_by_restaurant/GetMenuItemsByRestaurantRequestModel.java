package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant;

import java.util.UUID;

public record GetMenuItemsByRestaurantRequestModel(
    UUID restaurantId,
    int page,
    int size
) {}
