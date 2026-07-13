package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetAllRestaurantsParams(
    @Schema(example = "Cantina") String name,
    @Schema(example = "12345678000199") String taxIdentifier,
    @Schema(example = "Italiana") String cuisineType,
    @Schema(example = "1") Integer page,
    @Schema(example = "20") Integer size
) {
    public GetAllRestaurantsParams {
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
