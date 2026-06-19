package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class RestaurantDsGatewayImpl implements RestaurantDsGateway {

    private final RestaurantJpaRepository jpaRepository;

    public RestaurantDsGatewayImpl(RestaurantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RestaurantDsResponseModel save(RestaurantDsRequestModel restaurant) {
        RestaurantEntity saved = jpaRepository.save(RestaurantEntityMapper.toEntity(restaurant));
        return RestaurantEntityMapper.toDsResponse(saved);
    }

    @Override
    public Optional<RestaurantDsResponseModel> findById(UUID id) {
        return jpaRepository.findById(id).map(RestaurantEntityMapper::toDsResponse);
    }

    @Override
    public PageResult<RestaurantDsResponseModel> findAll(SearchQuery query, int page, int size) {
        Specification<RestaurantEntity> spec = SpecificationBuilder.build(
            query,
            RestaurantFilterFields.ALLOWED
        );

        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by("name").ascending()
        );
        Page<RestaurantEntity> resultPage = jpaRepository.findAll(spec, pageRequest);

        return new PageResult<>(
            resultPage.getContent().stream().map(RestaurantEntityMapper::toDsResponse).toList(),
            resultPage.getTotalElements(),
            page,
            size
        );
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
