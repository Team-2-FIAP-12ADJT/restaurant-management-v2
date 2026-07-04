package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id;

import java.math.BigDecimal;
import java.util.UUID;

public record GetMenuItemByIdResponseModel(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    boolean onlyLocal,
    String photoPath,
    UUID restaurantId
) {}
