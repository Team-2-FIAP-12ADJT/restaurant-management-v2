package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemDsGatewayImplTest {

    @Mock
    private MenuItemJpaRepository repository;

    private MenuItemDsGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        gateway = new MenuItemDsGatewayImpl(repository);
    }

    @Test
    void savesNewMenuItemAndMapsResponse() {
        MenuItemDsRequestModel request = request();
        when(repository.findById(request.id())).thenReturn(Optional.empty());
        when(repository.save(any(MenuItemEntity.class)))
            .thenAnswer(call -> call.getArgument(0));

        MenuItemDsResponseModel response = gateway.save(request);

        ArgumentCaptor<MenuItemEntity> captor =
            ArgumentCaptor.forClass(MenuItemEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(request.id(), response.id());
        assertEquals(request.restaurantId(), response.restaurantId());
        assertNotNull(captor.getValue().getCreatedAt());
        assertNotNull(captor.getValue().getUpdatedAt());
    }

    @Test
    void preservesCreatedAtWhenUpdating() {
        MenuItemDsRequestModel request = request();
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        MenuItemEntity current = entity(request);
        current.setCreatedAt(createdAt);

        when(repository.findById(request.id()))
            .thenReturn(Optional.of(current));
        when(repository.save(any(MenuItemEntity.class)))
            .thenAnswer(call -> call.getArgument(0));

        gateway.save(request);

        ArgumentCaptor<MenuItemEntity> captor =
            ArgumentCaptor.forClass(MenuItemEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(createdAt, captor.getValue().getCreatedAt());
    }

    @Test
    void findsExistingAndMissingMenuItem() {
        MenuItemDsRequestModel request = request();
        when(repository.findByIdAndDeletedAtIsNull(request.id()))
            .thenReturn(Optional.of(entity(request)))
            .thenReturn(Optional.empty());

        assertTrue(gateway.findById(request.id()).isPresent());
        assertTrue(gateway.findById(request.id()).isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void listsMenuItemsAndMenuItemsByRestaurant() {
        MenuItemDsRequestModel request = request();
        MenuItemEntity entity = entity(request);
        PageImpl<MenuItemEntity> page = new PageImpl<>(List.of(entity));

        when(
            repository.findAll(
                any(Specification.class),
                any(Pageable.class)
            )
        ).thenReturn(page);
        when(
            repository.findAllByRestaurantIdAndDeletedAtIsNull(
                any(UUID.class),
                any(Pageable.class)
            )
        ).thenReturn(page);

        var all = gateway.findAll(new SearchQuery(List.of()), 1, 10);
        var byRestaurant = gateway.findAllByRestaurant(
            request.restaurantId(),
            2,
            20
        );

        assertEquals(1, all.content().size());
        assertEquals(1, all.page());
        assertEquals(10, all.size());
        assertEquals(1, byRestaurant.content().size());
        assertEquals(2, byRestaurant.page());
        assertEquals(20, byRestaurant.size());
    }

    @Test
    void softDeletesExistingMenuItem() {
        MenuItemDsRequestModel request = request();
        MenuItemEntity entity = entity(request);
        when(repository.findByIdAndDeletedAtIsNull(request.id()))
            .thenReturn(Optional.of(entity));

        gateway.deleteById(request.id());

        assertNotNull(entity.getDeletedAt());
        verify(repository).save(entity);
    }

    @Test
    void ignoresDeleteWhenMenuItemIsMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndDeletedAtIsNull(id))
            .thenReturn(Optional.empty());

        gateway.deleteById(id);

        verify(repository, never()).save(any());
    }

    @Test
    void delegatesExistenceCheck() {
        UUID id = UUID.randomUUID();
        when(repository.existsByIdAndDeletedAtIsNull(id)).thenReturn(true);

        assertTrue(gateway.existsById(id));
        assertFalse(gateway.existsById(UUID.randomUUID()));
    }

    private static MenuItemDsRequestModel request() {
        return new MenuItemDsRequestModel(
            UUID.randomUUID(),
            "Risoto",
            "Risoto de cogumelos",
            new BigDecimal("39.90"),
            true,
            "/images/risoto.jpg",
            UUID.randomUUID()
        );
    }

    private static MenuItemEntity entity(MenuItemDsRequestModel request) {
        Instant now = Instant.now();
        return MenuItemEntity.builder()
            .id(request.id())
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .onlyLocal(request.onlyLocal())
            .photoPath(request.photoPath())
            .restaurant(
                RestaurantEntity.builder().id(request.restaurantId()).build()
            )
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
}
