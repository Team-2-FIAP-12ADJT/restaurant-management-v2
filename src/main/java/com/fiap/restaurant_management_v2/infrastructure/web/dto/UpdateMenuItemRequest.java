package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import com.fiap.restaurant_management_v2.domain.MenuItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMenuItemRequest(
    @NotBlank @Size(max = MenuItem.MAX_NAME_LENGTH) String name,
    @NotBlank @Size(max = MenuItem.MAX_DESCRIPTION_LENGTH) String description,
    @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2)
    BigDecimal price,
    @NotNull Boolean onlyLocal,
    @NotBlank @Size(max = MenuItem.MAX_PHOTO_PATH_LENGTH) String photoPath,
    @NotNull UUID restaurantId
) {}
