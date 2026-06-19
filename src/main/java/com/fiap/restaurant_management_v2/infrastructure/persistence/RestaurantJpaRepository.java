package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RestaurantJpaRepository
    extends
        JpaRepository<RestaurantEntity, UUID>,
        JpaSpecificationExecutor<RestaurantEntity> {
}
