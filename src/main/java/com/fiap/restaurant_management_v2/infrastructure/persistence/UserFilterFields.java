package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.Map;

public class UserFilterFields {

    private UserFilterFields() {}

    static final Map<String, String> ALLOWED = Map.of(
        "name",
        "name",
        "email",
        "email",
        "login",
        "login"
    );
}
