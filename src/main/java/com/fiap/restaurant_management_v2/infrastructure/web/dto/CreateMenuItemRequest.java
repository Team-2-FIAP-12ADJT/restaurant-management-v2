package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateMenuItemRequest(
    @NotBlank String name,
    @NotBlank String description,
    @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2)
    BigDecimal price,
    @NotNull Boolean onlyLocal,
    @NotBlank String photoPath,
    @NotNull UUID restaurantId
) {}
