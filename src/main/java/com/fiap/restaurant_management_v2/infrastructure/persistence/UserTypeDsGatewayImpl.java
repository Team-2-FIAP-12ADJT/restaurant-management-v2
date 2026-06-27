package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.*;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class UserTypeDsGatewayImpl implements UserTypeDsGateway {

    private final UserTypeJpaRepository jpaRepository;

    public UserTypeDsGatewayImpl(UserTypeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserTypeDsResponseModel save(UserTypeDsRequestModel userType) {
        UserTypeEntity saved = jpaRepository.save(
            UserTypeEntityMapper.toEntity(userType)
        );
        return UserTypeEntityMapper.toDsResponse(saved);
    }

    @Override
    public boolean existsByUserType(String userType) {
        return jpaRepository.existsByUserTypeAndDeletedAtIsNull(userType);
    }

    @Override
    public PageResult<UserTypeDsResponseModel> findAll(
        SearchQuery query,
        int page,
        int size
    ) {
        Specification<UserTypeEntity> spec = SpecificationBuilder.build(
            query,
            UserFilterFields.ALLOWED
        );

        Specification<UserTypeEntity> notDeleted = (root, q, cb) ->
            cb.isNull(root.get("deletedAt"));
        spec = spec.and(notDeleted);

        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by("userType").ascending()
        );

        Page<UserTypeEntity> resultPage = jpaRepository.findAll(
            spec,
            pageRequest
        );

        return new PageResult<>(
            resultPage
                .getContent()
                .stream()
                .map(UserTypeEntityMapper::toDsResponse)
                .toList(),
            resultPage.getTotalElements(),
            page,
            size
        );
    }

    @Override
    public Optional<UserTypeDsResponseModel> findById(UUID id) {
        return jpaRepository
            .findByIdAndDeletedAtIsNull(id)
            .map(UserTypeEntityMapper::toDsResponse);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.findByIdAndDeletedAtIsNull(id).ifPresent(entity -> {
            entity.setDeletedAt(Instant.now());
            jpaRepository.save(entity);
        });
    }
}
