package com.fiap.restaurant_management_v2.application.gateways;

import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.Optional;
import java.util.UUID;

public interface MenuItemDsGateway {
    MenuItemDsResponseModel save(MenuItemDsRequestModel menuItem);

    Optional<MenuItemDsResponseModel> findById(UUID id);

    PageResult<MenuItemDsResponseModel> findAll(SearchQuery query, int page, int size);

    PageResult<MenuItemDsResponseModel> findAllByRestaurant(
        UUID restaurantId,
        int page,
        int size
    );

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
