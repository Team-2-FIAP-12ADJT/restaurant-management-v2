package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GetAllRestaurantsParamsTest {

    @Test
    void defaultsPageAndSizeWhenNullOrInvalid() {
        var p1 = new GetAllRestaurantsParams(null, null, null, null, null);
        assertEquals(1, p1.page());
        assertEquals(10, p1.size());

        var p2 = new GetAllRestaurantsParams(null, null, null, -1, 500);
        assertEquals(1, p2.page());
        assertEquals(100, p2.size());

        var p3 = new GetAllRestaurantsParams(null, null, null, 2, 0);
        assertEquals(2, p3.page());
        assertEquals(10, p3.size());
    }
}
