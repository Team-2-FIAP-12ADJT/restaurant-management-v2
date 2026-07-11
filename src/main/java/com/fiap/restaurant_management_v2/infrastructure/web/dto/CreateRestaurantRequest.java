package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record CreateRestaurantRequest(
    @NotBlank @Schema(example = "Cantina da Nona") String name,
    @NotBlank @Schema(example = "Rua das Flores, 123 - São Paulo, SP") String address,
    @NotBlank
    @Pattern(
        regexp = "^([A-Z0-9]{12}[0-9]{2}|[A-Z0-9]{2}\\.[A-Z0-9]{3}\\.[A-Z0-9]{3}/[A-Z0-9]{4}-[0-9]{2})$",
        message = "Tax identifier must be a valid CNPJ (14 digits, with or without formatting)"
    )
    @Schema(example = "12345678000199") String taxIdentifier,
    @NotBlank @Schema(example = "Italiana") String cuisineType,
    @NotBlank @Schema(example = "Seg-Sáb 11:00-23:00") String openingHours,
    @NotNull @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID ownerId
) {
    // Construtor SÓ normaliza — formato vai para @Pattern (erro por campo).
    // CNPJ: remove só a máscara (./-), preservando alfanumérico, p/ guardar 14
    // chars crus (coluna VARCHAR(14)). @Pattern valida o valor já normalizado.
    public CreateRestaurantRequest {
        name = name != null ? name.trim() : null;
        address = address != null ? address.trim() : null;
        openingHours = openingHours != null ? openingHours.trim() : null;
        cuisineType = cuisineType != null ? cuisineType.trim() : null;
        taxIdentifier = taxIdentifier != null
            ? taxIdentifier.replaceAll("[./-]", "").toUpperCase()
            : null;
    }
}
