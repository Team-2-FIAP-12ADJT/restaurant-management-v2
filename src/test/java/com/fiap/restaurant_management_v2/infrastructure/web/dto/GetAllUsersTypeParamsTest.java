package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GetAllUsersTypeParams — Pagination Validation")
class GetAllUsersTypeParamsTest {

    // --- RAMIFICAÇÕES DO CAMPO PÁGINA ---
    @Test
    @DisplayName("page: null defaults to 1")
    void pageNullDefaults() {
        var params = new GetAllUsersTypeParams(null, null);
        assertEquals(1, params.page());
    }

    @Test
    @DisplayName("page: zero defaults to 1 (invalid page)")
    void pageZeroDefaults() {
        var params = new GetAllUsersTypeParams(0, null);
        assertEquals(1, params.page());
    }

    @Test
    @DisplayName("page: negative defaults to 1 (invalid page)")
    void pageNegativeDefaults() {
        var params = new GetAllUsersTypeParams(-5, null);
        assertEquals(1, params.page());
    }

    @Test
    @DisplayName("page: positive value is preserved")
    void pagePositivePreserved() {
        var params = new GetAllUsersTypeParams(5, null);
        assertEquals(5, params.page());
    }

    @Test
    @DisplayName("page: 1 is valid and preserved")
    void pageOnePreserved() {
        var params = new GetAllUsersTypeParams(1, null);
        assertEquals(1, params.page());
    }

    // --- RAMIFICAÇÕES DO CAMPO TAMANHO ---
    @Test
    @DisplayName("size: null defaults to 10")
    void sizeNullDefaults() {
        var params = new GetAllUsersTypeParams(1, null);
        assertEquals(10, params.size());
    }

    @Test
    @DisplayName("size: zero defaults to 10 (invalid size)")
    void sizeZeroDefaults() {
        var params = new GetAllUsersTypeParams(1, 0);
        assertEquals(10, params.size());
    }

    @Test
    @DisplayName("size: negative defaults to 10 (invalid size)")
    void sizeNegativeDefaults() {
        var params = new GetAllUsersTypeParams(1, -5);
        assertEquals(10, params.size());
    }

    @Test
    @DisplayName("size: 1-99 is preserved")
    void sizeSmallPreserved() {
        var params = new GetAllUsersTypeParams(1, 50);
        assertEquals(50, params.size());
    }

    @Test
    @DisplayName("size: 100 is preserved (max allowed)")
    void sizeMaxPreserved() {
        var params = new GetAllUsersTypeParams(1, 100);
        assertEquals(100, params.size());
    }

    @Test
    @DisplayName("size: values > 100 are capped to 100")
    void sizeCappedAt100() {
        var params = new GetAllUsersTypeParams(1, 500);
        assertEquals(100, params.size());
    }

    @Test
    @DisplayName("size: 150 is capped to 100")
    void sizeLargeValueCapped() {
        var params = new GetAllUsersTypeParams(1, 150);
        assertEquals(100, params.size());
    }

    // --- TESTES DE COMBINAÇÃO ---
    @Test
    @DisplayName("page and size: both null default appropriately")
    void bothPaginationNullDefaults() {
        var params = new GetAllUsersTypeParams(null, null);
        assertEquals(1, params.page());
        assertEquals(10, params.size());
    }

    @Test
    @DisplayName("page and size: both invalid default appropriately")
    void bothPaginationInvalidDefaults() {
        var params = new GetAllUsersTypeParams(-1, 0);
        assertEquals(1, params.page());
        assertEquals(10, params.size());
    }

    @Test
    @DisplayName("page and size: both valid are preserved")
    void bothPaginationValidPreserved() {
        var params = new GetAllUsersTypeParams(3, 25);
        assertEquals(3, params.page());
        assertEquals(25, params.size());
    }

    @Test
    @DisplayName("page and size: size > 100 is capped even when page is valid")
    void sizeCappedWithValidPage() {
        var params = new GetAllUsersTypeParams(5, 200);
        assertEquals(5, params.page());
        assertEquals(100, params.size());
    }
}

