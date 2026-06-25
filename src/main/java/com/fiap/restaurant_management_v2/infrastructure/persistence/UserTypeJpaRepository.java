package com.fiap.restaurant_management_v2.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserTypeJpaRepository
    extends
        JpaRepository<UserTypeEntity, UUID>,
        JpaSpecificationExecutor<UserTypeEntity>
{
    boolean existsByUserType(String userType);

    Optional<UserTypeEntity> findByIdAndDeletedAtIsNull(UUID id);
    /*
    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);

     */
}
