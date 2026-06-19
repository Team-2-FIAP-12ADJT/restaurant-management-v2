package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import java.time.Instant;

final class RestaurantEntityMapper {
    private RestaurantEntityMapper() {}

    static RestaurantEntity toEntity(RestaurantDsRequestModel restaurant) {
        Instant now = Instant.now();
        return new RestaurantEntity(
            restaurant.id(),
            restaurant.name(),
            restaurant.address(),
            restaurant.cuisineType(),
            restaurant.openingHours(),
            restaurant.ownerId(),
            now,
            now
        );
    }

    static RestaurantDsResponseModel toDsResponse(RestaurantEntity entity) {
        return new RestaurantDsResponseModel(
            entity.getId(),
            entity.getName(),
            entity.getAddress(),
            entity.getCuisineType(),
            entity.getOpeningHours(),
            entity.getOwnerId()
        );
    }
}
