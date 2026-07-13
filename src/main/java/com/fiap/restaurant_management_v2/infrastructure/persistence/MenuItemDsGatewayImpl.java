package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.exception.MenuItemNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class MenuItemDsGatewayImpl implements MenuItemDsGateway {

    private final MenuItemJpaRepository jpaRepository;

    public MenuItemDsGatewayImpl(MenuItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MenuItemDsResponseModel save(MenuItemDsRequestModel menuItem) {
        MenuItemEntity saved = jpaRepository.save(
            MenuItemEntityMapper.toEntity(menuItem, null)
        );
        return MenuItemEntityMapper.toDsResponse(saved);
    }

    @Override
    public MenuItemDsResponseModel update(MenuItemDsRequestModel menuItem) {
        MenuItemEntity entity = jpaRepository
            .findByIdAndDeletedAtIsNull(menuItem.id())
            .orElseThrow(() ->
                new MenuItemNotFoundException(
                    "Item do cardápio não encontrado: " + menuItem.id()
                )
            );
        entity.setName(menuItem.name());
        entity.setDescription(menuItem.description());
        entity.setPrice(menuItem.price());
        entity.setOnlyLocal(menuItem.onlyLocal());
        entity.setPhotoPath(menuItem.photoPath());
        entity.setRestaurant(
            RestaurantEntity.builder().id(menuItem.restaurantId()).build()
        );
        entity.setUpdatedAt(Instant.now());

        return MenuItemEntityMapper.toDsResponse(jpaRepository.save(entity));
    }

    @Override
    public Optional<MenuItemDsResponseModel> findById(UUID id) {
        return jpaRepository
            .findByIdAndDeletedAtIsNull(id)
            .map(MenuItemEntityMapper::toDsResponse);
    }

    @Override
    public PageResult<MenuItemDsResponseModel> findAll(
        SearchQuery query,
        int page,
        int size
    ) {
        Specification<MenuItemEntity> specification =
            SpecificationBuilder.build(query, MenuItemFilterFields.ALLOWED);
        Specification<MenuItemEntity> notDeleted = (root, q, cb) ->
            cb.isNull(root.get("deletedAt"));

        Page<MenuItemEntity> resultPage = jpaRepository.findAll(
            specification.and(notDeleted),
            pageRequest(page, size)
        );

        return toPageResult(resultPage, page, size);
    }

    @Override
    public PageResult<MenuItemDsResponseModel> findAllByRestaurant(
        UUID restaurantId,
        int page,
        int size
    ) {
        Page<MenuItemEntity> resultPage =
            jpaRepository.findAllByRestaurantIdAndDeletedAtIsNull(
                restaurantId,
                pageRequest(page, size)
            );

        return toPageResult(resultPage, page, size);
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
    public boolean existsByRestaurantIdAndIsActive(UUID restaurantId) {
        return jpaRepository.existsByRestaurantIdAndDeletedAtIsNull(restaurantId);
    }

    private static PageRequest pageRequest(int page, int size) {
        return PageRequest.of(
            page - 1,
            size,
            Sort.by("name").ascending()
        );
    }

    private static PageResult<MenuItemDsResponseModel> toPageResult(
        Page<MenuItemEntity> resultPage,
        int page,
        int size
    ) {
        return new PageResult<>(
            resultPage
                .getContent()
                .stream()
                .map(MenuItemEntityMapper::toDsResponse)
                .toList(),
            resultPage.getTotalElements(),
            page,
            size
        );
    }
}
