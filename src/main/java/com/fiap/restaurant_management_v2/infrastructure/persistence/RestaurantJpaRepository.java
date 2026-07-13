package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RestaurantJpaRepository
    extends
        JpaRepository<RestaurantEntity, UUID>,
        JpaSpecificationExecutor<RestaurantEntity>
{
    boolean existsByIdAndDeletedAtIsNull(UUID id);

    Optional<RestaurantEntity> findByIdAndDeletedAtIsNull(UUID id);

    Optional<RestaurantEntity> findByTaxIdentifierAndDeletedAtIsNull(
        String taxIdentifier
    );

    boolean existsByTaxIdentifierAndDeletedAtIsNull(String taxIdentifier);

    boolean existsByOwnerIdAndDeletedAtIsNull(UUID ownerId);

    boolean existsByTaxIdentifierAndDeletedAtIsNullAndIdNot(
        String taxIdentifier,
        UUID id
    );

    List<RestaurantEntity> findAllByOwnerIdInAndDeletedAtIsNull(
        Collection<UUID> ownerIds
    );
}
