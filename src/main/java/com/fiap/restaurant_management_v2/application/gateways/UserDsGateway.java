package com.fiap.restaurant_management_v2.application.gateways;

import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data source gateway for users. Owned by the application circle, implemented in
 * infrastructure. Speaks DS-models only — the domain {@code User} entity never
 * crosses this boundary.
 */
public interface UserDsGateway {
    UserDsResponseModel save(UserDsRequestModel user);

    UserDsResponseModel update(
        UUID id,
        String name,
        String email,
        String login,
        String taxIdentifier
    );

    Optional<UserDsResponseModel> findById(UUID id);

    Optional<UserCredentialDsResponseModel> findByLogin(String login);

    List<UserDsResponseModel> findAllByIds(Collection<UUID> ids);

    Optional<UserBindDsResponseModel> findAllById(UUID id);

    boolean existsByTaxIdentifierExcludingId(String taxIdentifier, UUID id);

    boolean existsByEmailExcludingId(String email, UUID id);

    boolean existsByLoginExcludingId(String login, UUID id);

    boolean existsByTaxIdentifier(String taxIdentifier);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsById(UUID id);

    PageResult<UserDsResponseModel> findAll(
        SearchQuery query,
        int page,
        int size
    );

    void deleteById(UUID id);

    void bindUserType(UUID userId, UUID typeId);

    void unbindUserType(UUID typeId);

    List<UUID> findActiveIdsByUserTypeId(UUID typeId);
}
