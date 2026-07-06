package com.fiap.restaurant_management_v2.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.RestaurantNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantDsGatewayImplAdditionalTest {

    @Mock
    private RestaurantJpaRepository repository;

    private RestaurantDsGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        gateway = new RestaurantDsGatewayImpl(repository);
    }

    @Test
    void existsByIdDelegatesToRepository() {
        UUID id = UUID.randomUUID();
        when(repository.existsByIdAndDeletedAtIsNull(id)).thenReturn(true);

        assertTrue(gateway.existsById(id));

        verify(repository).existsByIdAndDeletedAtIsNull(id);
    }

    @Test
    void existsByOwnerIdAndIsActiveDelegatesToRepository() {
        UUID ownerId = UUID.randomUUID();
        when(repository.existsByOwnerIdAndDeletedAtIsNull(ownerId)).thenReturn(false);

        assertFalse(gateway.existsByOwnerIdAndIsActive(ownerId));

        verify(repository).existsByOwnerIdAndDeletedAtIsNull(ownerId);
    }

    @Test
    void existsByTaxIdentifierDelegatesToRepository() {
        String taxIdentifier = "12345678000199";
        when(repository.existsByTaxIdentifierAndDeletedAtIsNull(taxIdentifier)).thenReturn(true);

        assertTrue(gateway.existsByTaxIdentifier(taxIdentifier));

        verify(repository).existsByTaxIdentifierAndDeletedAtIsNull(taxIdentifier);
    }

    @Test
    void updateThrowsWhenRestaurantDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () ->
            gateway.update(
                id,
                "Cantina",
                "Rua A",
                "12345678000199",
                "Italiana",
                "09:00-18:00",
                UUID.randomUUID()
            )
        );
    }
}
