package com.fiap.restaurant_management_v2.application.gateways;

import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantDsGateway {
    RestaurantDsResponseModel save(RestaurantDsRequestModel restaurant);

    Optional<RestaurantDsResponseModel> findById(UUID id);

    Optional<RestaurantDsResponseModel> findByTaxIdentifier(
        String taxIdentifier
    );

    PageResult<RestaurantDsResponseModel> findAll(
        SearchQuery query,
        int page,
        int size
    );

    void deleteById(UUID id);

    boolean existsById(UUID id);

    boolean existsByTaxIdentifier(String taxIdentifier);

    boolean existsByCnpjExcludingId(String taxIdentifier, UUID id);

    boolean existsByOwnerIdAndIsActive(UUID ownerId);

    RestaurantDsResponseModel update(
        UUID id,
        String name,
        String address,
        String taxIdentifier,
        String cuisineType,
        String openingHours,
        UUID ownerId
    );

    List<RestaurantDsResponseModel> findAllByOwnerIds(Collection<UUID> ownerIds);
}
