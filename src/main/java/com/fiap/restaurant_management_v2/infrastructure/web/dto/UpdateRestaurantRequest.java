package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record UpdateRestaurantRequest(
    @Nullable @Schema(example = "Cantina da Nona") String name,
    @Nullable @Schema(example = "Rua das Flores, 123 - São Paulo, SP") String address,
    @Nullable
    @Pattern(
        regexp = "^(?:[A-Z0-9]{12}\\d{2}|[A-Z0-9]{2}\\.[A-Z0-9]{3}\\.[A-Z0-9]{3}/[A-Z0-9]{4}-\\d{2})$",
        message = "Tax identifier must be a valid CNPJ (14 digits, with or without formatting)"
    )
    @Schema(example = "12345678000199") String taxIdentifier,
    @Nullable @Schema(example = "Italiana") String cuisineType,
    @Nullable @Schema(example = "Seg-Sáb 11:00-23:00") String openingHours,
    @Nullable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID ownerId
) {
    // PATCH parcial: campo ausente (null) = mantém. Construtor SÓ normaliza —
    // CNPJ tira só a máscara (./-), preservando alfanumérico; formato vai p/
    // @Pattern (valida o já normalizado; ignora null).
    public UpdateRestaurantRequest {
        name = name != null ? name.trim() : null;
        address = address != null ? address.trim() : null;
        cuisineType = cuisineType != null ? cuisineType.trim() : null;
        openingHours = openingHours != null ? openingHours.trim() : null;
        taxIdentifier = taxIdentifier != null
            ? taxIdentifier.replaceAll("[./-]", "").toUpperCase()
            : null;
    }
}
