package com.fiap.restaurant_management_v2.application.gateways;

import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.UUID;

/**
 * Data source gateway for users. Owned by the application circle, implemented in
 * infrastructure. Speaks DS-models only — the domain {@code User} entity never
 * crosses this boundary.
 */
public interface UserDsGateway {
    UserDsResponseModel save(UserDsRequestModel user);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsById(UUID id);

    PageResult<UserDsResponseModel> findAll(
        SearchQuery query,
        int page,
        int size
    );
}
