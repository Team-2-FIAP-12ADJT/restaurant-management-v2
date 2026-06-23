package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

import java.math.BigDecimal;

public record MenuItemViewModel(
    String id,
    String name,
    String description,
    BigDecimal price,
    boolean onlyLocal,
    String photoPath,
    String restaurantId
) {}
