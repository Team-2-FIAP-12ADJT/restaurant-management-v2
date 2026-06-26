package com.fiap.restaurant_management_v2.infrastructure.web.dto;

public record GetAllRestaurantsParams(
    String name,
    String cuisineType,
    Integer page,
    Integer size
) {
    public GetAllRestaurantsParams {
        page = (page == null || page < 1) ? 1 : page;
        size = (size == null || size < 1) ? 10 : Math.min(size, 100);
    }
}
