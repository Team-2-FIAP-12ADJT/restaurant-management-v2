package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.Optional;
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
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
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

    @Override
    public Optional<UserDsResponseModel> findByIdAndDeletedAtIsNull(
        java.util.UUID id
    ) {
        return jpaRepository
            .findByIdAndDeletedAtIsNull(id)
            .map(UserEntityMapper::toDsResponse);
    }
}
