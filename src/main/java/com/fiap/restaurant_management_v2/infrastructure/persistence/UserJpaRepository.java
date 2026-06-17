package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserJpaRepository
    extends
        JpaRepository<UserEntity, UUID>,
        JpaSpecificationExecutor<UserEntity>
{
    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);
}
