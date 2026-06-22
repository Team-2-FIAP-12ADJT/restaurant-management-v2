package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateRestaurantRequest(
    @NotBlank String name,
    @NotBlank String address,
    @NotBlank String cuisineType,
    @NotBlank String openingHours,
    @NotNull UUID ownerId
) {}
