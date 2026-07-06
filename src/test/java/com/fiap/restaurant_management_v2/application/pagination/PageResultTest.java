package com.fiap.restaurant_management_v2.application.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageResultTest {

    @Test
    void totalPagesZeroWhenSizeIsZero() {
        PageResult<String> p = new PageResult<>(List.of(), 10L, 1, 0);
        assertEquals(0, p.totalPages());
    }

    @Test
    void totalPagesExactDivision() {
        PageResult<String> p = new PageResult<>(List.of(), 20L, 1, 10);
        assertEquals(2, p.totalPages());
    }

    @Test
    void totalPagesWithRemainder() {
        PageResult<String> p = new PageResult<>(List.of(), 21L, 1, 10);
        assertEquals(3, p.totalPages());
    }
}

