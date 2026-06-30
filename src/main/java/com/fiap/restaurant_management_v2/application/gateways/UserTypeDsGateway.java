package com.fiap.restaurant_management_v2.application.gateways;

import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;

import java.util.Optional;
import java.util.UUID;

/**
 * Data source gateway for users Type. Owned by the application circle, implemented in
 * infrastructure. Speaks DS-models only — the domain {@code UserType} entity never
 * crosses this boundary.
 */
public interface UserTypeDsGateway {
    UserTypeDsResponseModel save(UserTypeDsRequestModel user);

    boolean existsByUserType(String userType);

    PageResult<UserTypeDsResponseModel> findAll(
            SearchQuery query,
            int page,
            int size
    );

    Optional<UserTypeDsResponseModel> findById(UUID id);

    void deleteById(UUID id);
}
