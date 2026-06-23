package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MenuItemParamsTest {

    @Test
    void appliesPaginationDefaultsAndLimits() {
        GetAllMenuItemsParams defaults =
            new GetAllMenuItemsParams(null, null, null);
        GetAllMenuItemsParams normalized =
            new GetAllMenuItemsParams("Risoto", 0, 500);
        GetAllMenuItemsParams invalidSize =
            new GetAllMenuItemsParams("Risoto", 1, 0);
        GetAllMenuItemsParams valid =
            new GetAllMenuItemsParams("Risoto", 2, 20);
        GetMenuItemsByRestaurantParams restaurantDefaults =
            new GetMenuItemsByRestaurantParams(null, null);
        GetMenuItemsByRestaurantParams restaurantNormalized =
            new GetMenuItemsByRestaurantParams(-1, 0);
        GetMenuItemsByRestaurantParams restaurantValid =
            new GetMenuItemsByRestaurantParams(3, 30);

        assertEquals(1, defaults.page());
        assertEquals(10, defaults.size());
        assertEquals("Risoto", normalized.name());
        assertEquals(1, normalized.page());
        assertEquals(100, normalized.size());
        assertEquals(10, invalidSize.size());
        assertEquals(2, valid.page());
        assertEquals(20, valid.size());
        assertEquals(1, restaurantDefaults.page());
        assertEquals(10, restaurantDefaults.size());
        assertEquals(1, restaurantNormalized.page());
        assertEquals(10, restaurantNormalized.size());
        assertEquals(3, restaurantValid.page());
        assertEquals(30, restaurantValid.size());
    }
}
