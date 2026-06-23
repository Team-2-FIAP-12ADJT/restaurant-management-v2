package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.Map;

final class MenuItemFilterFields {
    private MenuItemFilterFields() {}

    static final Map<String, String> ALLOWED = Map.of(
        "name", "name"
    );
}
