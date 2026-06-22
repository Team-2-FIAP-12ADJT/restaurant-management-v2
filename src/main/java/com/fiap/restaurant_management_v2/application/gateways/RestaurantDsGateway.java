package com.fiap.restaurant_management_v2.application.gateways;

import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantDsGateway {
    RestaurantDsResponseModel save(RestaurantDsRequestModel restaurant);

    Optional<RestaurantDsResponseModel> findById(UUID id);

    PageResult<RestaurantDsResponseModel> findAll(SearchQuery query, int page, int size);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
