package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserTypeJpaRepository
    extends
        JpaRepository<UserTypeEntity, UUID>,
        JpaSpecificationExecutor<UserTypeEntity>
{
    boolean existsByUserType(String userType);

    Optional<UserTypeEntity> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByUserTypeAndDeletedAtIsNull(String userType);
    /*
    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);

     */
}
