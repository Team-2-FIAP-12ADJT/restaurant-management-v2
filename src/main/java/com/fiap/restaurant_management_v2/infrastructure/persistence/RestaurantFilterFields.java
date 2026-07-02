package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.Map;

public class RestaurantFilterFields {

    private RestaurantFilterFields() {}

    static final Map<String, String> ALLOWED = Map.of(
        "name", "name",
        "cuisineType", "cuisineType",
        "taxIdentifier", "taxIdentifier"
    );
}
