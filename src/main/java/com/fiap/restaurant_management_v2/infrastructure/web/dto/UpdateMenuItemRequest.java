package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import com.fiap.restaurant_management_v2.domain.MenuItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMenuItemRequest(
    @Nullable @Size(max = MenuItem.MAX_NAME_LENGTH) @Schema(example = "Pizza Margherita") String name,
    @Nullable @Size(max = MenuItem.MAX_DESCRIPTION_LENGTH) @Schema(example = "Molho de tomate, mozzarella e manjericão fresco") String description,
    @Nullable @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) @Schema(example = "49.90") BigDecimal price,
    @Nullable @Schema(example = "false") Boolean onlyLocal,
    @Nullable @Size(max = MenuItem.MAX_PHOTO_PATH_LENGTH) @Schema(example = "/images/menu/pizza-margherita.jpg") String photoPath,
    @Nullable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID restaurantId
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
