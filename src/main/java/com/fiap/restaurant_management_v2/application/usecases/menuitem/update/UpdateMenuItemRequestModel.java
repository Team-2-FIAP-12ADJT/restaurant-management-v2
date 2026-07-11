package com.fiap.restaurant_management_v2.application.usecases.menuitem.update;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMenuItemRequestModel(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    Boolean onlyLocal,
    String photoPath,
    UUID restaurantId
) {}
