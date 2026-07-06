package com.fiap.restaurant_management_v2.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fiap.restaurant_management_v2.application.gateways.RestaurantDsRequestModel;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RestaurantEntityMapperTest {

    @Test
    void toEntityPreservesCreatedAtWhenProvided() {
        var id = UUID.randomUUID();
        var owner = UUID.randomUUID();
        var req = new RestaurantDsRequestModel(id, "N", "A", "123", "C", "OH", owner);
        Instant created = Instant.parse("2020-01-01T00:00:00Z");

        var entity = RestaurantEntityMapper.toEntity(req, created);

        assertEquals(created, entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void toEntitySetsCreatedAtWhenNull() {
        var id = UUID.randomUUID();
        var owner = UUID.randomUUID();
        var req = new RestaurantDsRequestModel(id, "N", "A", "123", "C", "OH", owner);

        var entity = RestaurantEntityMapper.toEntity(req, null);

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }
}

