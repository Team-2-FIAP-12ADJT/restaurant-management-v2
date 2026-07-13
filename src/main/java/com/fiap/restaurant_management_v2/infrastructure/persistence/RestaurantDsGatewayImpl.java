package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
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
        Instant createdAt = jpaRepository
            .findById(restaurant.id())
            .map(RestaurantEntity::getCreatedAt)
            .orElse(null);
        RestaurantEntity saved = jpaRepository.save(
            RestaurantEntityMapper.toEntity(restaurant, createdAt)
        );
        return RestaurantEntityMapper.toDsResponse(saved);
    }

    @Override
    public Optional<RestaurantDsResponseModel> findById(UUID id) {
        return jpaRepository
            .findByIdAndDeletedAtIsNull(id)
            .map(RestaurantEntityMapper::toDsResponse);
    }

    @Override
    public Optional<RestaurantDsResponseModel> findByTaxIdentifier(
        String taxIdentifier
    ) {
        return jpaRepository
            .findByTaxIdentifierAndDeletedAtIsNull(taxIdentifier)
            .map(RestaurantEntityMapper::toDsResponse);
    }

    @Override
    public PageResult<RestaurantDsResponseModel> findAll(
        SearchQuery query,
        int page,
        int size
    ) {
        Specification<RestaurantEntity> spec = SpecificationBuilder.build(
            query,
            RestaurantFilterFields.ALLOWED
        );

        Specification<RestaurantEntity> notDeleted = (root, q, cb) ->
            cb.isNull(root.get("deletedAt"));
        spec = spec.and(notDeleted);

        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by("name").ascending()
        );
        Page<RestaurantEntity> resultPage = jpaRepository.findAll(
            spec,
            pageRequest
        );

        return new PageResult<>(
            resultPage
                .getContent()
                .stream()
                .map(RestaurantEntityMapper::toDsResponse)
                .toList(),
            resultPage.getTotalElements(),
            page,
            size
        );
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.findByIdAndDeletedAtIsNull(id).ifPresent(entity -> {
            entity.setDeletedAt(Instant.now());
            jpaRepository.save(entity);
        });
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsByIdAndDeletedAtIsNull(id);
    }

    @Override
    public boolean existsByCnpjExcludingId(String taxIdentifier, UUID id) {
        return jpaRepository.existsByTaxIdentifierAndDeletedAtIsNullAndIdNot(
            taxIdentifier,
            id
        );
    }

    @Override
    public boolean existsByTaxIdentifier(String taxIdentifier) {
        return jpaRepository.existsByTaxIdentifierAndDeletedAtIsNull(
            taxIdentifier
        );
    }

    @Override
    public boolean existsByOwnerIdAndIsActive(UUID ownerId) {
        return jpaRepository.existsByOwnerIdAndDeletedAtIsNull(ownerId);
    }

    @Override
    public RestaurantDsResponseModel update(
        UUID id,
        String name,
        String address,
        String taxIdentifier,
        String cuisineType,
        String openingHours,
        UUID ownerId
    ) {
        RestaurantEntity entity = jpaRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() ->
                new RestaurantNotFoundException("Restaurant not found")
            );
        entity.setName(name);
        entity.setAddress(address);
        entity.setTaxIdentifier(taxIdentifier);
        entity.setCuisineType(cuisineType);
        entity.setOpeningHours(openingHours);
        entity.setOwner(UserEntity.builder().id(ownerId).build());
        entity.setUpdatedAt(Instant.now());

        return RestaurantEntityMapper.toDsResponse(jpaRepository.save(entity));
    }

    @Override
    public List<RestaurantDsResponseModel> findAllByOwnerIds(
        Collection<UUID> ownerIds
    ) {
        if (ownerIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository
            .findAllByOwnerIdInAndDeletedAtIsNull(ownerIds)
            .stream()
            .map(RestaurantEntityMapper::toDsResponse)
            .toList();
    }
}
