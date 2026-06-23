package com.fiap.restaurant_management_v2.application.gateways;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemDsRequestModel(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    boolean onlyLocal,
    String photoPath,
    UUID restaurantId
) {}
