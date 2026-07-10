package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import com.fiap.restaurant_management_v2.domain.MenuItem;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMenuItemRequest(
    @Nullable @Size(max = MenuItem.MAX_NAME_LENGTH) String name,
    @Nullable @Size(max = MenuItem.MAX_DESCRIPTION_LENGTH) String description,
    @Nullable @DecimalMin("0.01") @Digits(integer = 8, fraction = 2)
    BigDecimal price,
    @Nullable Boolean onlyLocal,
    @Nullable @Size(max = MenuItem.MAX_PHOTO_PATH_LENGTH) String photoPath,
    @Nullable UUID restaurantId
) {
    // PATCH parcial: campo ausente (null) = mantém o atual. Construtor SÓ
    // normaliza (trim); campo presente-blank vira "" → cai na validação do
    // domínio (estado mesclado) → 400, não no-op silencioso.
    public UpdateMenuItemRequest {
        name = name != null ? name.trim() : null;
        description = description != null ? description.trim() : null;
        photoPath = photoPath != null ? photoPath.trim() : null;
    }
}
