package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import java.time.Instant;

final class RestaurantEntityMapper {
    private RestaurantEntityMapper() {}

    static RestaurantEntity toEntity(RestaurantDsRequestModel restaurant, Instant createdAt) {
        Instant now = Instant.now();
        boolean isNew = createdAt == null;
        return RestaurantEntity.builder()
            .id(restaurant.id())
            .name(restaurant.name())
            .address(restaurant.address())
            .cuisineType(restaurant.cuisineType())
            .openingHours(restaurant.openingHours())
            .owner(UserEntity.builder().id(restaurant.ownerId()).build())
            .createdAt(isNew ? now : createdAt)
            .updatedAt(now)
            .build();
    }

    static RestaurantDsResponseModel toDsResponse(RestaurantEntity entity) {
        return new RestaurantDsResponseModel(
            entity.getId(),
            entity.getName(),
            entity.getAddress(),
            entity.getCuisineType(),
            entity.getOpeningHours(),
            entity.getOwner().getId()
        );
    }
}
