package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuItemJpaRepository
    extends
        JpaRepository<MenuItemEntity, UUID>,
        JpaSpecificationExecutor<MenuItemEntity> {

    boolean existsByIdAndDeletedAtIsNull(UUID id);

    Optional<MenuItemEntity> findByIdAndDeletedAtIsNull(UUID id);

    Page<MenuItemEntity> findAllByRestaurantIdAndDeletedAtIsNull(
        UUID restaurantId,
        Pageable pageable
    );
}
