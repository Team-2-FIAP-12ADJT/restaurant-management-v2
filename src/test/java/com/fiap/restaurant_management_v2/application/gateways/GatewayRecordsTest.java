package com.fiap.restaurant_management_v2.application.gateways;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class GatewayRecordsTest {

    @Test
    void userTypeUpdateDsRequestModelExposesComponents() {
        UUID id = UUID.randomUUID();

        var model = new UserTypeUpdateDsRequestModel(id, "admin");

        assertEquals(id, model.id());
        assertEquals("admin", model.userType());
    }
}
