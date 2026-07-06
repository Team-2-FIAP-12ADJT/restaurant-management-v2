package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class BindUserTypeRequestTest {

    @Test
    void bindUserTypeRequestExposesComponents() {
        UUID userId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();

        var request = new BindUserTypeRequest(userId, typeId);

        assertEquals(userId, request.userId());
        assertEquals(typeId, request.typeId());
    }
}
