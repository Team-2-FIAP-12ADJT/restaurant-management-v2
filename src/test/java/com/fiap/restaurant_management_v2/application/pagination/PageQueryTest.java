package com.fiap.restaurant_management_v2.application.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PageQueryTest {

    @Test
    void pageQueryExposesComponents() {
        var query = new PageQuery(3, 50);

        assertEquals(3, query.page());
        assertEquals(50, query.size());
    }
}
