package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository
    extends
        JpaRepository<UserEntity, UUID>,
        JpaSpecificationExecutor<UserEntity>
{
    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByLoginAndDeletedAtIsNull(String login);

    boolean existsByIdAndDeletedAtIsNull(UUID id);

    boolean existsByEmailAndDeletedAtIsNullAndIdNot(String email, UUID id);

    boolean existsByLoginAndDeletedAtIsNullAndIdNot(String login, UUID id);

    boolean existsByTaxIdentifierAndDeletedAtIsNullAndIdNot(
        String taxIdentifier,
        UUID id
    );

    @EntityGraph(attributePaths = "userTypeEntity")
    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);

    @Query("select u from UserEntity u left join fetch u.userTypeEntity where u.login = :login and u.deletedAt is null")
    Optional<UserEntity> findByLoginAndDeletedAtIsNull(@Param("login") String login);

    @EntityGraph(attributePaths = "userTypeEntity")
    List<UserEntity> findAllByIdInAndDeletedAtIsNull(Collection<UUID> ids);

    boolean existsByTaxIdentifierAndDeletedAtIsNull(String taxIdentifier);

    List<UserEntity> findAllByUserTypeEntityIdAndDeletedAtIsNull(UUID typeId);

    @Override
    @EntityGraph(attributePaths = "userTypeEntity")
    Page<UserEntity> findAll(Specification<UserEntity> spec, Pageable pageable);
}
