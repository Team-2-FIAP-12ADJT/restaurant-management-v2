package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import java.time.Instant;

final class MenuItemEntityMapper {
    private MenuItemEntityMapper() {}

    static MenuItemEntity toEntity(
        MenuItemDsRequestModel menuItem,
        Instant createdAt
    ) {
        Instant now = Instant.now();
        boolean isNew = createdAt == null;

        return MenuItemEntity.builder()
            .id(menuItem.id())
            .name(menuItem.name())
            .description(menuItem.description())
            .price(menuItem.price())
            .onlyLocal(menuItem.onlyLocal())
            .photoPath(menuItem.photoPath())
            .restaurant(
                RestaurantEntity.builder().id(menuItem.restaurantId()).build()
            )
            .createdAt(isNew ? now : createdAt)
            .updatedAt(now)
            .build();
    }

    static MenuItemDsResponseModel toDsResponse(MenuItemEntity entity) {
        return new MenuItemDsResponseModel(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            entity.isOnlyLocal(),
            entity.getPhotoPath(),
            entity.getRestaurant().getId()
        );
    }
}
