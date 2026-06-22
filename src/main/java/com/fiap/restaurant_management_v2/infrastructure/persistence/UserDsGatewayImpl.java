package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class UserDsGatewayImpl implements UserDsGateway {

    private final UserJpaRepository jpaRepository;

    public UserDsGatewayImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserDsResponseModel save(UserDsRequestModel user) {
        UserEntity saved = jpaRepository.save(UserEntityMapper.toEntity(user));
        return UserEntityMapper.toDsResponse(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLoginAndDeletedAtIsNull(login);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Optional<UserDsResponseModel> findById(UUID id) {
        return jpaRepository
                .findByIdAndDeletedAtIsNull(id)
                .map(UserEntityMapper::toDsResponse);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.findByIdAndDeletedAtIsNull(id).ifPresent(entity -> {
            entity.setDeletedAt(Instant.now());
            jpaRepository.save(entity);
        });
    }

    @Override
    public PageResult<UserDsResponseModel> findAll(
            SearchQuery query,
            int page,
            int size
    ) {
        Specification<UserEntity> spec = SpecificationBuilder.build(
                query,
                UserFilterFields.ALLOWED
        );

        Specification<UserEntity> notDeleted = (root, q, cb) ->
                cb.isNull(root.get("deletedAt"));
        spec = spec.and(notDeleted);

        PageRequest pageRequest = PageRequest.of(
                page - 1,
                size,
                Sort.by("name").ascending()
        );
        Page<UserEntity> resultPage = jpaRepository.findAll(spec, pageRequest);

        return new PageResult<>(
                resultPage
                        .getContent()
                        .stream()
                        .map(UserEntityMapper::toDsResponse)
                        .toList(),
                resultPage.getTotalElements(),
                page,
                size
        );
    }

}
